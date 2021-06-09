package fun.mortnon.flyrafter.mvn.base;

import fun.mortnon.flyrafter.FlyRafter;
import fun.mortnon.flyrafter.FlyRafterBuilder;
import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.mvn.db.DatasourceFactory;
import fun.mortnon.flyrafter.mvn.resolver.ResourceFactory;
import fun.mortnon.flyrafter.mvn.utils.Utils;
import fun.mortnon.flyrafter.resolver.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class FlyrafterExecutor {
    private List<String> compilePaths = new ArrayList<>();
    private File resourceFolder;
    private MavenProject project;

    private List<String> includePackages;

    private List<File> compileFiles = new ArrayList<>();
    private List<File> sourceFiles = new ArrayList<>();

    public FlyrafterExecutor(File resourceFolder, MavenProject project, List<String> includePackages) {
        this.resourceFolder = resourceFolder;
        this.project = project;

        this.includePackages = includePackages;

        try {
            this.compilePaths = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            Utils.LOGGER.error("load compile class path fail for " + e);
        }
    }

    public void startup() {
        Optional.ofNullable(compilePaths).ifPresent(paths -> paths.forEach(path -> resolveResource(path)));
    }

    private void resolveResource(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Set<File> files = matchFile(dir);
            Map<String, Object> configurationMap = new HashMap<>();
            files.forEach(k -> {
                Map<String, Object> map = ResourceFactory.getResolver(k).resolveResource(k);
                if (null != map) {
                    configurationMap.putAll(map);
                }
            });
            if (configurationMap.size() == 0) {
                Utils.LOGGER.info("get none application configuration file.");
            }
            executeFlyRafter(configurationMap);
        }
    }

    private Set<File> matchFile(File dir) {
        Set<File> fileList = new HashSet<>();
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                fileList.addAll(matchFile(f));
            } else {
                String name = f.getName();
                if (Utils.mathConfigurationFile(name)) {
                    fileList.add(f);
                }
            }
        }
        return fileList;
    }

    /**
     * 执行 FlyRafter
     *
     * @param configurationMap
     */
    private void executeFlyRafter(Map<String, Object> configurationMap) {
        FlyRafterConfiguration flyRafterConfiguration = addConfiguration(configurationMap);
        generateFolder(flyRafterConfiguration);
        DataSource dataSource = addDatasource(configurationMap);
        URLClassLoader classLoader = getClassLoader();

        FlyRafter flyRafter = new FlyRafterBuilder(flyRafterConfiguration, dataSource, classLoader, includePackages).build();
        flyRafter.startup();
        copyToSource();
    }

    /**
     * 从配置文件中获取相关配置
     *
     * @param configurationMap
     * @return
     */
    private FlyRafterConfiguration addConfiguration(Map<String, Object> configurationMap) {
        FlyRafterConfiguration flyRafterConfiguration = new FlyRafterConfiguration();

        if (configurationMap.containsKey(Commons.FLYRAFTER + Commons.ENABLED)) {
            flyRafterConfiguration.setEnabled(Boolean.valueOf(String.valueOf(configurationMap.get(Commons.FLYRAFTER + Commons.ENABLED))));
        }

        if (configurationMap.containsKey(Commons.FLYRAFTER + Commons.VERSION_PATTERN)) {
            flyRafterConfiguration.setVersionPattern(String.valueOf(configurationMap.get(Commons.FLYRAFTER + Commons.VERSION_PATTERN)));
        }

        if (configurationMap.containsKey(Commons.FLYRAFTER + Commons.LOCATIONS)) {
            ArrayList<String> locations = (ArrayList<String>) configurationMap.get(Commons.FLYRAFTER + Commons.LOCATIONS);
            flyRafterConfiguration.setLocations(locations);
        }

        if (configurationMap.containsKey(Commons.FLYRAFTER + Commons.BACKUP)) {
            flyRafterConfiguration.setBackup(String.valueOf(configurationMap.get(Commons.FLYRAFTER + Commons.BACKUP)));
        }

        if (configurationMap.containsKey(Commons.FLYRAFTER + Commons.MAP_TO_UNDERSCORE)) {
            flyRafterConfiguration.setMapToUnderscore(Boolean.valueOf(String.valueOf(configurationMap.get(Commons.FLYRAFTER + Commons.MAP_TO_UNDERSCORE))));
        }

        FlywayProperties flywayProperties = new FlywayProperties();

        if (configurationMap.containsKey(Commons.FLYWAY + Commons.PREFIX)) {
            flywayProperties.setSqlMigrationPrefix(String.valueOf(configurationMap.get(Commons.FLYWAY + Commons.PREFIX)));
        }

        if (configurationMap.containsKey(Commons.FLYWAY + Commons.SEPARATOR)) {
            flywayProperties.setSqlMigrationSeparator(String.valueOf(configurationMap.get(Commons.FLYWAY + Commons.SEPARATOR)));
        }

        if (configurationMap.containsKey(Commons.FLYWAY + Commons.SUFFIX)) {
            ArrayList<String> suffix = (ArrayList<String>) configurationMap.get(Commons.FLYWAY + Commons.SUFFIX);
            flywayProperties.setSqlMigrationSuffixes(suffix);
        }

        if (configurationMap.containsKey(Commons.FLYWAY + Commons.LOCATIONS)) {
            ArrayList<String> locations = (ArrayList<String>) configurationMap.get(Commons.FLYWAY + Commons.LOCATIONS);
            flywayProperties.setLocations(locations);
        }

        flyRafterConfiguration.setFlywayProperties(flywayProperties);

        return flyRafterConfiguration;
    }

    /**
     * 生成内存数据库连接
     *
     * @return
     */
    private DataSource addDatasource(Map<String, Object> configurationMap) {
        DataSource dataSource = null;
        try {
            String url = null;
            String driver = null;
            String username = null;
            String password = null;

            for (String key : configurationMap.keySet()) {
                if (key.startsWith(Commons.DATASOURCE)) {
                    if (key.endsWith(Commons.URL)) {
                        if (StringUtils.isBlank(url)) {
                            url = String.valueOf(configurationMap.get(key));
                        }
                    }

                    if (key.endsWith(Commons.DRIVER)) {
                        driver = String.valueOf(configurationMap.get(key));
                    }

                    if (key.endsWith(Commons.USERNAME)) {
                        username = String.valueOf(configurationMap.get(key));
                    }

                    if (key.endsWith(Commons.PASSWORD)) {
                        password = String.valueOf(configurationMap.get(key));
                    }
                }
            }

            dataSource = DatasourceFactory.create(url, driver, username, password);

            //TODO:当前直接连接本地环境的数据库。后续应该使用模拟数据库执行现有sql，再用于flyrafter比较生成sql文件。
//            if (null != compileFiles && compileFiles.size() > 0) {
//                DbExecutor dbExecutor = new DbExecutor(dataSource);
//                compileFiles.forEach(k -> dbExecutor.executeSql(k));
//            }

            return dataSource;
        } catch (Exception e) {
            Utils.LOGGER.error("create datasource fail.", e);
            return null;
        }
    }


    private URLClassLoader getClassLoader() {
        try {
            // 转为 URL 数组
            List<URL> urls = new ArrayList<>();

            for (int i = 0; i < compilePaths.size(); i++) {
                String filePath = compilePaths.get(i);
                if (filePath.endsWith(".jar")) {
                    urls.add(new URL("jar:" + new File(filePath).toURI().toURL() + "!/"));
                } else {
                    urls.add(new File(filePath).toURI().toURL());
                }
            }
            // 自定义类加载器
            return new URLClassLoader(urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
        } catch (
                Exception e) {
            Utils.LOGGER.debug("Couldn't get the classloader.");
            return null;
        }

    }


    /**
     * 将生成的文件复制到源目录中
     */
    private void copyToSource() {
        compileFiles.forEach(compileFile -> {
            sourceFiles.forEach(sourceFile -> {
                try {
                    if (compileFile.isDirectory() && compileFile.exists()) {
                        FileUtils.copyDirectory(compileFile, sourceFile);
                    }
                } catch (IOException e) {
                    Utils.LOGGER.error("copy sql to source folder fail.");
                }
            });
        });
    }

    private void generateFolder(FlyRafterConfiguration flyRafterConfiguration) {
        List<String> locations = flyRafterConfiguration.getLocations();
        compilePaths.forEach(k -> {
            locations.forEach(folder -> {
                String compilePath = k + File.separator + folder.replace(Constants.CLASSPATH, "");
                String sourcePath = resourceFolder.getPath() + File.separator + folder.replace(Constants.CLASSPATH, "");
                File compileFile = new File(compilePath);
                File sourceFile = new File(sourcePath);
                if (!sourceFile.exists()) {
                    sourceFile.mkdirs();
                }
                compileFiles.add(compileFile);
                sourceFiles.add(sourceFile);
            });
        });
    }


}
