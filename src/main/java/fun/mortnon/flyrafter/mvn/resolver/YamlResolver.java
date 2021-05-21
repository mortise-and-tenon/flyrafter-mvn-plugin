package fun.mortnon.flyrafter.mvn.resolver;

import fun.mortnon.flyrafter.mvn.utils.Utils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class YamlResolver implements ResourcesResolver {

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
        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileReader(file));
            propertyMap = readFullNameData(data,"");
        } catch (FileNotFoundException e) {
            Utils.LOGGER.error("load yaml file fail.");
        }

        return propertyMap;
    }

    private Map<String, Object> readFullNameData(Map<String, Object> detailMap, String prefix) {
        Map<String, Object> data = new HashMap<>();
        detailMap.forEach((key, value) -> {
            if (value instanceof Map) {
                Map<String, Object> detail = readFullNameData((Map<String, Object>) value, prefix + "." + key);
                if (null != detail) {
                    data.putAll(detail);
                }
            } else {
                String namePrefix = prefix.substring(1,prefix.length());
                data.put(namePrefix + "." + key, value);
            }
        });

        return data;

    }
}
