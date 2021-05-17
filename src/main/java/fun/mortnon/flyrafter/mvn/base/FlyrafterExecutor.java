package fun.mortnon.flyrafter.mvn.base;

import fun.mortnon.flyrafter.FlyRafter;
import fun.mortnon.flyrafter.FlyRafterBuilder;
import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.mvn.resolver.ResourceFactory;
import fun.mortnon.flyrafter.mvn.utils.Utils;
import fun.mortnon.flyrafter.resolver.FlyRafterUtils;
import org.apache.maven.model.Resource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class FlyrafterExecutor {
    private List<String> compilePaths;

    public FlyrafterExecutor(List<String> compilePaths) {
        this.compilePaths = compilePaths;
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
    }

    /**
     * 从配置文件中获取相关配置
     *
     * @param configurationMap
     * @return
     */
    private FlyRafterConfiguration addConfiguration(Map<String, Object> configurationMap) {
        FlyRafterConfiguration flyRafterConfiguration = new FlyRafterConfiguration();
        //TODO:如果未启用相应功能，返回null，不进行相应操作
        return flyRafterConfiguration;
    }

    /**
     * 生成内存数据库连接
     *
     * @return
     */
    private DataSource addDatasource() {
        //TODO:
        DataSource dataSource = null;
        return dataSource;
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


}
