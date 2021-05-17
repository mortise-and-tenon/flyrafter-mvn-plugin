package fun.mortnon.flyrafter.mvn;

import fun.mortnon.flyrafter.mvn.base.FlyrafterExecutor;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/5/13
 */
@Mojo(name = "generate")
public class FlyrafterMojo extends AbstractMojo {
    @Parameter(defaultValue = "${basedir}")
    private File baseDir;

    @Parameter(defaultValue = "${project.build.resources}", readonly = true, required = true)
    private List<Resource> resources;

    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    private List<String> compilePaths;

    private FlyrafterExecutor executor;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("FlyRafter Startup.");
        executor = new FlyrafterExecutor(this.compilePaths);
        executor.startup();
        getLog().info("FlyRafter Terminate.");
    }
}
