package fun.mortnon.flyrafter.mvn.base;

import fun.mortnon.flyrafter.FlyRafter;
import fun.mortnon.flyrafter.FlyRafterBuilder;
import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.mvn.resolver.ResourceFactory;
import fun.mortnon.flyrafter.mvn.utils.Utils;
import org.apache.maven.model.Resource;

import javax.sql.DataSource;
import java.io.File;
import java.util.*;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class FlyrafterExecutor {
    private List<Resource> resources;

    public FlyrafterExecutor(List<Resource> resources) {
        this.resources = resources;
    }

    public void startup() {
        Optional.ofNullable(resources).ifPresent(list -> list.forEach(resource -> resolveResource(resource)));
    }

    private void resolveResource(Resource resource) {
        String directory = resource.getDirectory();
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            Set<File> files = matchFile(dir);
            Map<String, Object> configurationMap = new HashMap<>();
            files.forEach(k -> {
                Map<String, Object> map = ResourceFactory.getResolver(k).resolveResource(k);
                configurationMap.putAll(map);
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
                fileList.addAll(matchFile(dir));
            }
            String name = f.getName();
            if (Utils.mathConfigurationFile(name)) {
                fileList.add(f);
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
        FlyRafter flyRafter = new FlyRafterBuilder(flyRafterConfiguration, dataSource).build();
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
}
