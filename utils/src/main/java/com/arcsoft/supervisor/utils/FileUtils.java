package com.arcsoft.supervisor.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file.
 *
 * @author zw.
 */
public abstract class FileUtils {

    /**
     * A matcher interface used for filter file or dir.
     */
    public interface PathMatcher {

        /**
         * Determine the file is matched or not.
         *
         * <p>Skip this file if the file is not matched.
         *
         * @param file the path of file
         * @return {@code true} or {@code false} if the file is not matched
         */
        boolean isFileMatch(Path file);

        /**
         * Determine the dir is matched or not.
         *
         * <p>Skip all of children if the dir is not matched.
         *
         * @param dir the path of dir
         * @return {@code true} or {@code false} if the dir is not matched
         */
        boolean isDirMatch(Path dir);
    }

    /**
     * PathMatcher adapter for subclass to do extend.
     *
     */
    public static abstract class PathMatcherAdapter implements PathMatcher {
        @Override
        public boolean isFileMatch(Path file) {
            return true;
        }

        @Override
        public boolean isDirMatch(Path dir) {
            return true;
        }
    }

    /**
     * Find all matched children files under {@code basePath} with {@code matcher}.
     *
     * @param basePath the base path to be find
     * @param matcher the matcher used for find matched path
     * @return all matched files
     * @throws IOException if an I/O error occurs
     */
    public static List<File> findAllFiles(String basePath, final PathMatcher matcher) throws IOException {
        final List<File> matchedPaths = new ArrayList<>();
        Path start = Paths.get(basePath);
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (matcher.isFileMatch(file)) {
                    matchedPaths.add(file.toFile());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return matcher.isDirMatch(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
            }
        });
        return matchedPaths;
    }

}
