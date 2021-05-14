package fun.mortnon.flyrafter.mvn.resolver;

import org.apache.maven.model.Resource;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Moon Wu
 * @date 2021/5/14
 */
public class YamlResolver implements ResourcesResolver{

    @Override
    public Map<String, Object> resolveResource(File file) {
        return null;
    }

    @Override
    public Object resolveResource(File file, String key) {
        return null;
    }
}
