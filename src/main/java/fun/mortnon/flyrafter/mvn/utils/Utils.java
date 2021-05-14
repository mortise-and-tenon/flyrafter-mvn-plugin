package fun.mortnon.flyrafter.mvn.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class Utils {
    public static final Log LOGGER = new SystemStreamLog();

    private static final String configNamePrefix = "application";

    private static List<String> yamlConfigName;

    private static List<String> propertiesConfigName;

    static {
        yamlConfigName = new ArrayList<>();
        yamlConfigName.add(".yml");
        yamlConfigName.add(".yaml");

        propertiesConfigName = new ArrayList<>();
        propertiesConfigName.add(".properties");
    }

    /**
     * 检查文件名为spring boot的配置文件
     *
     * @param name
     * @return
     */
    public static boolean mathConfigurationFile(String name) {
        return StringUtils.isNotBlank(name) && name.toLowerCase().startsWith(configNamePrefix)
                && (isYaml(name) || isProperties(name));
    }

    /**
     * 检测文件为 yaml 类型
     *
     * @param name
     * @return
     */
    public static boolean isYaml(String name) {
        return yamlConfigName.stream().map(k -> name.toLowerCase().endsWith(k)).reduce(false, (b1, b2) -> b1 || b2);
    }

    /**
     * 检测文件为 properties 类型
     *
     * @param name
     * @return
     */
    public static boolean isProperties(String name) {
        return propertiesConfigName.stream().map(k -> name.toLowerCase().endsWith(k)).reduce(false, (b1, b2) -> b1 || b2);
    }
}
