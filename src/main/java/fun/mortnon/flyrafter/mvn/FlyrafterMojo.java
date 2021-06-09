package fun.mortnon.flyrafter.mvn;

import fun.mortnon.flyrafter.mvn.base.FlyrafterExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/5/13
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class FlyrafterMojo extends AbstractMojo {
    @Parameter(defaultValue = "${basedir}/src/main/resources")
    private File resourceFolder;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "includePackages")
    private List<String> includePackages;

    private FlyrafterExecutor executor;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("FlyRafter Startup.");
        executor = new FlyrafterExecutor(this.resourceFolder, this.project, this.includePackages);
        executor.startup();
        getLog().info("FlyRafter Terminate.");
    }
}
