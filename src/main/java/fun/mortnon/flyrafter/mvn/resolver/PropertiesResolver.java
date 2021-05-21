package fun.mortnon.flyrafter.mvn.resolver;

import fun.mortnon.flyrafter.mvn.utils.Utils;
import org.apache.maven.model.Resource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class PropertiesResolver implements ResourcesResolver {


    @Override
    public Map<String, Object> resolveResource(File file) {
        return read(file);
    }

    @Override
    public Object resolveResource(File file, String key) {
        return read(file).getOrDefault(key, "");
    }

    private Map<String, Object> read(File file) {
        Map<String, Object> propertyMap = new HashMap<>();
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
            properties.entrySet().forEach(entry -> {
                propertyMap.put((String) entry.getKey(), entry.getValue());
            });
        } catch (IOException e) {
            Utils.LOGGER.error("load properties file fail.");
        }
        return propertyMap;
    }
}
