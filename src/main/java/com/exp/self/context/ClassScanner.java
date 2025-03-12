package com.exp.self.context;

import com.exp.self.annotation.Component;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClassScanner {
    public static Set<Class<?>> findClasses() throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        String path = "";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Classpath resource not found: " + path);
        }

        File directory = new File(resource.getFile());

        if (directory.exists()) {
            scanDirectory(directory, path, classes);
        } else {
            throw new IllegalArgumentException("Directory " + directory.getAbsolutePath() + " not found");
        }

        return classes;
    }

    private static void scanDirectory(File directory, String packageName, Set<Class<?>> classes) throws ClassNotFoundException {

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + (packageName.isEmpty() ? "" : ".") + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + (packageName.isEmpty() ? "" : ".") + file.getName().substring(0, file.getName().length() - 6);
                System.out.println("Found class " + className);

                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Component.class)) {
                    System.out.println("Class " + clazz.getName() + " is annotated with @Component");
                    classes.add(clazz);
                }
            }
        }

    }
}

