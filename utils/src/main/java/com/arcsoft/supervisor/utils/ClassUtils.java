package com.arcsoft.supervisor.utils;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * Utility for class.
 *
 * @author zw.
 */
public abstract class ClassUtils {

    /** The package separator character '.' */
    private static final char PACKAGE_SEPARATOR = '.';

    /** The path separator character '/' */
    private static final char PATH_SEPARATOR = '/';

    /** The inner class separator string "$" */
    private static final String INNER_CLASS_SEPARATOR = "$";

    /** The ".class" file suffix */
    public static final String CLASS_FILE_SUFFIX = ".class";


    /**
     * Find all of class under specific base package.
     *
     *
     * @param basePackage the base package to be find
     * @return all of class under base package
     * @throws IOException if an I/O error occurs
     * @throws URISyntaxException if failed to get resource from package
     * @throws ClassNotFoundException if failed to find the given class
     */
    public static List<Class> findAllClass(String basePackage) throws IOException, URISyntaxException, ClassNotFoundException {
        Objects.requireNonNull(basePackage, "The base package can't be null");
        String basePath = basePackage.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resourceUrls = contextClassLoader.getResources(basePath);
        List<File> files = new ArrayList<>();
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            files.addAll(FileUtils.findAllFiles(url.toURI().getPath(), new FileUtils.PathMatcherAdapter() {
                @Override
                public boolean isFileMatch(java.nio.file.Path file) {
                    //Skip inner class
                    return !file.getFileName().toString().contains(INNER_CLASS_SEPARATOR);
                }
            }));
        }

        List<Class> allClass = new ArrayList<>();
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            filePath = filePath.substring(0, filePath.length() - CLASS_FILE_SUFFIX.length());
            String qualifiedCls = filePath.substring(filePath.indexOf(basePath)).replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
            allClass.add(Class.forName(qualifiedCls));
        }
        return allClass;
    }

    /**
     * Method proxy to {@link Class#forName(String)}, but just return {@code null}
     * instead of thrown exception.
     *
     * @param className the class name
     * @return Class or {@code null} if class not found
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Just ignore
        }
        return null;
    }

    public static Class<?> forName(String className, boolean initialize) {
        try {
            return Class.forName(className, initialize, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            // Just ignore
        }
        return null;
    }
}
