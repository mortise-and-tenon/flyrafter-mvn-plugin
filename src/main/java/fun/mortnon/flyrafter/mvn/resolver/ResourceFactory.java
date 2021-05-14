package fun.mortnon.flyrafter.mvn.resolver;

import fun.mortnon.flyrafter.mvn.utils.Utils;

import java.io.File;
import java.util.NoSuchElementException;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class ResourceFactory {
    public static ResourcesResolver getResolver(File file) {
        if (Utils.isYaml(file.getName())) {
            return new YamlResolver();
        } else if (Utils.isProperties(file.getName())) {
            return new PropertiesResolver();
        }

        throw new NoSuchElementException("no match resolver for file name:" + file.getName());
    }
}
