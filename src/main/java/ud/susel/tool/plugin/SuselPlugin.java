package ud.susel.tool.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import ud.susel.tool.plugin.impl.ModuleNameParser;
import ud.susel.tool.plugin.impl.SuselMetadataTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Gradle {@link Plugin} that generate metadata on the module's service providers used by Susel.
 */
public class SuselPlugin implements Plugin<Project> {

    public static String SUSEL_METADATA_TASK_NAME = "suselMetadata";

    private static final Logger LOGGER = Logging.getLogger(SuselPlugin.class);

    @Override
    public void apply(Project project) {
        LOGGER.debug("Applying Susel plugin to " + project.getName());
        project.getPluginManager().apply(JavaBasePlugin.class);

        project.afterEvaluate(evaluatedProject -> {
            Configuration config = addToolDependency(evaluatedProject);

            evaluatedProject.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
                SuselMetadataTask metadataTask = createSuselMetadataTask(project, config);
                configureJavaCompileTask(project, metadataTask);
                configureJarTask(project, metadataTask);
            });
        });
    }

    private Configuration addToolDependency(Project project) {
        Configuration config = project.getConfigurations().create("susel")
                .setVisible(false)
                .setDescription("Process and create metadata for all service providers.");

        config.defaultDependencies(dependencies ->
                dependencies.add(project.getDependencies().create("ud.susel:tool:0.1.0-SNAPSHOT")));

        return config;
    }

    private SuselMetadataTask createSuselMetadataTask(Project project, Configuration config) {
        SuselMetadataTask metadataTask = project.getTasks()
                .create(SUSEL_METADATA_TASK_NAME, SuselMetadataTask.class);

        FileCollection outputClassDir = getOutputClassDir(project);
        metadataTask.setClasspath(outputClassDir);

        File srcClassDir = getSrcClassDir(project);
        if (srcClassDir == null) {
            throw new RuntimeException("Java source folder cannot be null for project: " + project.getName());
        }

        String moduleName = new ModuleNameParser(srcClassDir).parse();
        LOGGER.debug("Susel plugin to generate metadata for module: " + moduleName);

        metadataTask.doFirst(action -> {
            metadataTask.classpath(config);
            metadataTask.setMain("ud.susel.tool.Launcher");

            List<String> args = new ArrayList<>();
            args.add("--module-path");
            args.add(metadataTask.getClasspath().getAsPath());
            args.add("--add-modules");
            args.add(moduleName);
            args.add("--module");
            args.add("ud.susel.tool/" + metadataTask.getMain());
            args.add("--module-name");
            args.add(moduleName);
            args.add("--meta-inf-root-path");
            args.add(outputClassDir.getAsPath());

            metadataTask.setJvmArgs(args);
        });

        return metadataTask;
    }

    private void configureJavaCompileTask(Project project, SuselMetadataTask metadataTask) {
        JavaCompile javaCompile = (JavaCompile) project.getTasks().findByName(JavaPlugin.COMPILE_JAVA_TASK_NAME);
        metadataTask.dependsOn(javaCompile);

        javaCompile.doFirst(action -> {
            JavaCompile jc = (JavaCompile) action;
            FileCollection classPath = jc.getClasspath();
            metadataTask.classpath(classPath);
        });
    }

    private void configureJarTask(Project project, SuselMetadataTask metadataTask) {
        Jar jarTask = (Jar) project.getTasks().getByName("jar");
        jarTask.dependsOn(metadataTask);
    }

    private FileCollection getOutputClassDir(Project project) {
        return project.getConvention()
                .getPlugin(JavaPluginConvention.class)
                .getSourceSets()
                .getByName("main")
                .getOutput()
                .getClassesDirs();
    }

    private File getSrcClassDir(Project project) {
        Set<File> srcDirs = project.getConvention()
                .getPlugin(JavaPluginConvention.class)
                .getSourceSets()
                .getByName("main")
                .getJava()
                .getSrcDirs();

        File srcDir = null;

        for (File file : srcDirs) {
            srcDir = file;
        }

        return srcDir;
    }
}
