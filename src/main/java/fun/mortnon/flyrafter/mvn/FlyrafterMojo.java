package fun.mortnon.flyrafter.mvn;

import fun.mortnon.flyrafter.mvn.base.FlyrafterExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/5/13
 */
@Mojo(name = "generate")
public class FlyrafterMojo extends AbstractMojo {
    @Parameter(defaultValue = "${basedir}/src/main/resources")
    private File resourceFolder;

    @Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
    private List<String> compilePaths;

    private FlyrafterExecutor executor;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("FlyRafter Startup.");
        executor = new FlyrafterExecutor(this.compilePaths,this.resourceFolder);
        executor.startup();
        getLog().info("FlyRafter Terminate.");
    }
}
