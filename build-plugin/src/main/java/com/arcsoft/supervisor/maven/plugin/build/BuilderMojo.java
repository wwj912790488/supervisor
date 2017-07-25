package com.arcsoft.supervisor.maven.plugin.build;

import com.arcsoft.supervisor.utils.app.Environment;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * @author zw.
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class BuilderMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}")
    private File projectDirectory;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter(alias = "profiles")
    private List<Profile> profilesConfig;

    private static final String DEFAULT_EXPIRED_DATE = "2999-12-31 23:59:59";

    @Parameter(property = "supervisor.expiredDate", defaultValue = DEFAULT_EXPIRED_DATE)
    private String expiredDate;

    /**
     * The activated profiles
     */
    @Parameter(property = "supervisor.profiles")
    private String activeProfileStr;

    @Parameter(property = "supervisor.version")
    private String version;

    /**
     * Defaults profiles if {@link #activeProfileStr} is empty or null
     */
    @Parameter(defaultValue = "production")
    private String defaultProfiles;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File persistProfileDir;

    @Parameter
    private List<Replace> replaces;

    private Replacer replacer;

    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor pluginDescriptor;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject currentProject;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    private List<String> activeProfileList;

    private List<String> excludeProfileList;

    public BuilderMojo() {
        this.replacer = new FileReplacer();
    }

    private boolean hasProfileConfigs() {
        return profilesConfig != null && profilesConfig.size() > 0;
    }

    private void resolveProfiles() throws MojoExecutionException {
        List<String> activeProfiles = new ArrayList<>();
        Collections.addAll(activeProfiles,
                StringUtils.isNotBlank(activeProfileStr) ? activeProfileStr.trim().split(",") : defaultProfiles.split(",")
        );
        checkActiveProfiles(activeProfiles);
        // filter active profiles
        for (Iterator<String> it = activeProfiles.iterator(); it.hasNext(); ) {
            String activeProfile = it.next();
            boolean isExisted = false;
            for (Profile profileCfg : profilesConfig) {
                if (activeProfile.equals(profileCfg.getId())) {
                    isExisted = true;
                }
            }
            if (!isExisted) {
                it.remove();
            }
        }
        checkActiveProfiles(activeProfiles);
        List<String> excludeProfiles = new ArrayList<>();
        for (Profile profileCfg : profilesConfig) {
            if (!activeProfiles.contains(profileCfg.getId())) {
                excludeProfiles.add(profileCfg.getId());
            }
        }
        this.activeProfileList = activeProfiles;
        this.excludeProfileList = excludeProfiles;
    }

    private void checkActiveProfiles(List<String> activeProfiles) throws MojoExecutionException {
        if (activeProfiles.size() == 0) {
            throw new MojoExecutionException("Can't found any profile setting");
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (hasProfileConfigs()) {
            resolveProfiles();
            replaceAllReplaces(this.replaces);
            for (Profile profileCfg : profilesConfig) {

                // execute includes for exclude profiles
                if (excludeProfileList.contains(profileCfg.getId())) {
                    if (profileCfg.getIncludes() != null) {
                        for (Include include : profileCfg.getIncludes()) {
                            excludeClassWithAnnotation(include.getClassAnnotation());
                            // execute patterns for exclude profiles
                            if (include.getPatterns() != null) {
                                try {
                                    deleteClassesAndFolders(scanIncludes(include.getPatterns().toArray(new String[include.getPatterns().size()])));
                                } catch (IOException e) {
                                    throw new MojoExecutionException("Failed to delete resources", e);
                                }
                            }
                        }
                    }

                }

                // execute replaces for active profiles
                if (activeProfileList.contains(profileCfg.getId())) {
                    replaceAllReplaces(profileCfg.getReplaces());
                }

            }
        }

        try {
            writeProfilesToFile();
        } catch (IOException | ConfigurationException e) {
            throw new MojoExecutionException("Failed to save active profiles", e);
        }
    }

    protected void writeProfilesToFile() throws IOException, ConfigurationException {
        File persistProfileFile = new File(persistProfileDir, Environment.ENV_FILE_NAME);
        PropertiesConfiguration configuration = new PropertiesConfiguration(persistProfileFile);
        configuration.setDelimiterParsingDisabled(true);
        if (hasProfileConfigs()) {
            configuration.setProperty(Environment.EnvKey.profiles.name(), StringUtils.join(activeProfileList.iterator(), ","));
        }
        configuration.setProperty(Environment.EnvKey.date.name(), "aght" + getExpiredDate());
        if (StringUtils.isNotEmpty(version)) {
            configuration.setProperty(Environment.EnvKey.version.name(), version);
        }
        configuration.save();
    }

    private String getExpiredDate() {
        expiredDate = StringUtils.isNotBlank(expiredDate) && StringUtils.isNotBlank(expiredDate.trim())
                ? expiredDate : DEFAULT_EXPIRED_DATE;
        String[] dateArr = expiredDate.split(" ");
        if (dateArr.length == 1) {
            expiredDate += " 23:59:59";
        }
        try {
            return Environment.ExpireChecker.encrypt(expiredDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return org.apache.commons.lang3.StringUtils.EMPTY;
    }

    protected void replaceAllReplaces(List<Replace> replaces) {
        if (replaces != null) {
            for (Replace replace : replaces) {
                try {
                    replaceResource(replace);
                } catch (IOException e) {
                    getLog().error(e);
                }
            }
        }
    }

    protected void replaceResource(Replace replace) throws IOException {
        replacer.replace(replace);
    }

    protected void excludeClassWithAnnotation(String annotation) throws MojoExecutionException {
        if (StringUtils.isNotBlank(annotation)) {
            List<String> excludeClasses;
            try {
                excludeClasses = findClassByAnnotation(annotation);
            } catch (ClassNotFoundException | MalformedURLException e) {
                throw new MojoExecutionException("", e);
            }

            deleteClasses(excludeClasses);
        }
    }

    protected void deleteClassesAndFolders(Pair<String[], String[]> classesAndFolders) throws IOException {
        deleteClasses(classesAndFolders.getLeft());
        for (String folder : classesAndFolders.getRight()) {
            File directory = new File(outputDirectory, folder);
            if (directory.exists() && directory.isDirectory()) {
                FileVisitor simpleFileVisitor = new FileVisitor();
                Files.walkFileTree(directory.toPath(), simpleFileVisitor);
                if (!simpleFileVisitor.hasFile) {
                    FileUtils.forceDelete(directory);
                }
            }
        }
    }


    /**
     * A visitor implementation to check the children file is exist or not.
     */
    private class FileVisitor extends SimpleFileVisitor<Path> {

        private boolean hasFile;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.toFile().isFile()) {
                hasFile = true;
                return FileVisitResult.TERMINATE;
            }
            return super.visitFile(file, attrs);
        }
    }

    /**
     * Synonym method with {@link #deleteClasses(List)}.
     *
     * @param classes
     */
    protected void deleteClasses(String[] classes) {
        deleteClasses(Arrays.asList(classes));
    }

    /**
     * Deletes all of file associated with specific classes.
     *
     * @param classes name of desired resources
     */
    protected void deleteClasses(List<String> classes) {
        if (classes != null) {
            for (String excludeClass : classes) {
                FileUtils.deleteQuietly(new File(outputDirectory, excludeClass));
            }
        }
    }

    /**
     * Returns all of classes annotation by <code>annotationCls</code>.
     *
     * @param annotationCls the string of class.e.g: <code>com.arcsoft.supervisor.commons.profile</code>
     * @return all of classes annotation by specific class
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     */
    protected List<String> findClassByAnnotation(String annotationCls) throws ClassNotFoundException, MalformedURLException {
        final ClassRealm classRealm = pluginDescriptor.getClassRealm();
        final File classes = new File(outputDirectory.getAbsolutePath());
        classRealm.addURL(classes.toURI().toURL());
        Set<Artifact> depArtifacts = currentProject.getArtifacts();
        for (Artifact artifact : depArtifacts) {
            classRealm.addURL(artifact.getFile().toURI().toURL());
        }
        Class annotationClz = Class.forName(annotationCls, false, Thread.currentThread().getContextClassLoader());
        Pair<String[], String[]> includeFilesAndDirectories = scanIncludes(new String[]{"**/*.class"});
        List<String> clzs = new ArrayList<>();
        String separator = SystemUtils.IS_OS_WINDOWS ? "\\\\" : "/";
        for (String cls : includeFilesAndDirectories.getLeft()) {
            Class clz = Class.forName(cls.substring(0, cls.length() - 6).replaceAll(separator, "."), false, Thread.currentThread().getContextClassLoader());
            if (clz != annotationClz && clz.isAnnotationPresent(annotationClz)) {
                clzs.add(cls);
            }
        }
        return clzs;
    }

    /**
     * Returns file and directory path matched by <code>includes</code>.
     *
     * @param includes the path match expression
     * @return a pair object contains matched files and directories path
     */
    protected Pair<String[], String[]> scanIncludes(String[] includes) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(includes);
        ds.setBasedir(outputDirectory);
        ds.scan();
        return new Pair<>(ds.getIncludedFiles(), ds.getIncludedDirectories());
    }


    protected class Pair<L, R> {
        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }

    }
}
