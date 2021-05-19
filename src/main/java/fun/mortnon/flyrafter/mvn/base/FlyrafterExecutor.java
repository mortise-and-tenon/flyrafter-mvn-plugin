package fun.mortnon.flyrafter.mvn.base;

import fun.mortnon.flyrafter.FlyRafter;
import fun.mortnon.flyrafter.FlyRafterBuilder;
import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.mvn.db.H2DataSource;
import fun.mortnon.flyrafter.mvn.resolver.ResourceFactory;
import fun.mortnon.flyrafter.mvn.utils.Utils;
import fun.mortnon.flyrafter.resolver.Constants;
import fun.mortnon.flyrafter.resolver.FlyRafterUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
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
    private List<String> compilePaths;
    private File resourceFolder;

    public FlyrafterExecutor(List<String> compilePaths, File resourceFolder) {
        this.compilePaths = compilePaths;
        this.resourceFolder = resourceFolder;
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
            executeFlyRafter(configurationMap);
        } else {
            Utils.LOGGER.info("get none application configuration file.");
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
        DataSource dataSource = addDatasource();
        ClassLoader classLoader = getClassLoader();
        FlyRafter flyRafter = new FlyRafterBuilder(flyRafterConfiguration, dataSource, classLoader).build();
        FlyRafterUtils.setClassLoader(classLoader);
        flyRafter.startup();
        copyToSource(flyRafterConfiguration);
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
    private DataSource addDatasource() {
        DataSource dataSource = null;
        try {
            dataSource = H2DataSource.create();
            return dataSource;
        } catch (Exception e) {
            Utils.LOGGER.error("create h2 datasource fail.", e);
            return null;
        }
    }

    private ClassLoader getClassLoader() {
        try {
            // 转为 URL 数组
            URL urls[] = new URL[compilePaths.size()];
            for (int i = 0; i < compilePaths.size(); i++) {
                urls[i] = new File(compilePaths.get(i)).toURI().toURL();
            }
            // 自定义类加载器
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (Exception e) {
            Utils.LOGGER.debug("Couldn't get the classloader.");
            return null;
        }
    }

    /**
     * 将生成的文件复制到源目录中
     *
     * @param flyRafterConfiguration
     */
    private void copyToSource(FlyRafterConfiguration flyRafterConfiguration) {
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
                try {
                    FileUtils.copyDirectory(compileFile, sourceFile);
                } catch (IOException e) {
                    Utils.LOGGER.error("copy sql to source folder fail.");
                }
            });
        });
    }


}
