package fun.mortnon.flyrafter.mvn;

import org.apache.maven.model.Resource;
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
@Mojo(name="generate")
public class FlyrafterMojo extends AbstractMojo {
    @Parameter(defaultValue = "${basedir}")
    private File baseDir;

    @Parameter(defaultValue = "${project.build.resources}",readonly = true,required = true)
    private List<Resource> resources;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Hello,world");
        resources.forEach(k->{
            getLog().info(k.getDirectory());
        });
        getLog().info("end");
    }
}
