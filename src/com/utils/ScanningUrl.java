package com.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.annotations.ControllerAnnotation;
import com.annotations.HandleUrl;
import com.annotations.GetMapping;
import com.annotations.PostMapping;

public class ScanningUrl {
    // Charge dynamiquement toutes les classes d'un package donné
    public static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) return new ArrayList<>();

        File directory = new File(resource.getFile());
        return findClasses(directory, packageName);
    }

    // Parcourt récursivement un répertoire pour trouver toutes les classes d'un package
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) return classes;

        File[] files = directory.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replaceAll("\\.class$", "");
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    // Scanne les contrôleurs annotés et construit la map URL -> Liste de MappingHandler
    public static Map<String, List<MappingHandler>> scanUrlMappings(String packageName) throws Exception {
        Map<String, List<MappingHandler>> urlMappings = new HashMap<>();
        List<Class<?>> controllers = getClasses(packageName);
        for (Class<?> clazz : controllers) {
            if (clazz.isAnnotationPresent(ControllerAnnotation.class)) {
                for (Method method : clazz.getDeclaredMethods()) {
                    String url = null;
                    String httpMethod = null;

                    if (method.isAnnotationPresent(PostMapping.class)) {
                        url = method.getAnnotation(PostMapping.class).value();
                        httpMethod = "POST";
                    } else if (method.isAnnotationPresent(GetMapping.class)) {
                        url = method.getAnnotation(GetMapping.class).value();
                        httpMethod = "GET";
                    } else if (method.isAnnotationPresent(HandleUrl.class)) {
                        url = method.getAnnotation(HandleUrl.class).value();
                        httpMethod = "ALL";
                    }

                    if (url != null) {
                        if (url == null) url = "";
                        if (!url.isEmpty() && !url.startsWith("/")) {
                            url = "/" + url;
                        }
                        if (url.isEmpty()) {
                            url = "/";
                        }

                        UrlPattern pattern = new UrlPattern(url);
                        MappingHandler handler = new MappingHandler(clazz, method, pattern, httpMethod);
                        urlMappings.computeIfAbsent(url, k -> new java.util.ArrayList<>()).add(handler);
                    }
                }
            }
        }
        return urlMappings;
    }
}
