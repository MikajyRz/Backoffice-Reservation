package test.java;

import com.annotations.ControllerAnnotation;
import com.annotations.HandleUrl;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Scanner pour trouver tous les contrôleurs et leurs handleurl.
 * Affiche les URLs complètes construites à partir des annotations.
 */
public class ControllerAnnotationScanner {
    public static void main(String[] args) throws Exception {
        String packageName = "test.java"; // package racine à scanner
        List<Class<?>> classes = getClasses(packageName);
        
        System.out.println("Recherche des contrôleurs avec @ControllerAnnotation...");
        System.out.println("============================================================\n");
        
        int controllerCount = 0;
        int mappingCount = 0;
        
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(ControllerAnnotation.class)) {
                controllerCount++;
                ControllerAnnotation controllerAnnotation = clazz.getAnnotation(ControllerAnnotation.class);
                String controllerPath = getControllerPath(clazz, controllerAnnotation);
                
                // AJOUT: Afficher le nom du fichier source
                String fileName = getSourceFileName(clazz);
                System.out.println("Fichier: " + fileName);
                System.out.println("Il existe de ControllerAnnotation");
                System.out.println("Controller: " + controllerPath);
                
                // Collecter les URLs et méthodes
                Method[] methods = clazz.getDeclaredMethods();
                ArrayList<String> urls = new ArrayList<>();
                ArrayList<String> methodNames = new ArrayList<>();
                
                for (Method method : methods) {
                    if (method.isAnnotationPresent(HandleUrl.class)) {
                        HandleUrl handleUrl = method.getAnnotation(HandleUrl.class);
                        String methodPath = handleUrl.value();
                        
                        if (methodPath.isEmpty()) {
                            methodPath = method.getName();
                        }
                        
                        urls.add(methodPath);
                        methodNames.add(method.getName());
                        mappingCount++;
                    }
                }
                
                // Afficher les URLs
                if (!urls.isEmpty()) {
                    System.out.print("urlmethode:");
                    for (int i = 0; i < urls.size(); i++) {
                        System.out.print(urls.get(i));
                        if (i < urls.size() - 1) {
                            System.out.print(",");
                        }
                    }
                    System.out.println();
                    
                    // Afficher les méthodes
                    System.out.print("methode:");
                    for (int i = 0; i < methodNames.size(); i++) {
                        System.out.print(methodNames.get(i));
                        if (i < methodNames.size() - 1) {
                            System.out.print(",");
                        }
                    }
                    System.out.println();
                } else {
                    System.out.println("urlmethode:");
                    System.out.println("methode:");
                }
                
                System.out.println();
            }
        }
        
        if (controllerCount == 0) {
            System.out.println("Aucun contrôleur avec @ControllerAnnotation trouve");
        }
        
        System.out.println("============================================================");
        System.out.println("Resume:");
        System.out.println("  - Controleurs trouves: " + controllerCount);
        System.out.println("  - Mappings trouves: " + mappingCount);
    }

    /**
     * Obtient le nom du fichier source correspondant à la classe
     */
    private static String getSourceFileName(Class<?> clazz) {
        try {
            // Chercher dans le répertoire source "java"
            File sourceDir = new File("java");
            return findJavaFileName(sourceDir, clazz.getSimpleName());
        } catch (Exception e) {
            // Fallback: utiliser le nom de la classe
            return clazz.getSimpleName() + ".java";
        }
    }
    
    /**
     * Cherche récursivement le fichier .java qui contient la classe
     */
    private static String findJavaFileName(File directory, String className) {
        if (!directory.exists() || !directory.isDirectory()) {
            return className + ".java";
        }
        
        File[] files = directory.listFiles();
        if (files == null) return className + ".java";
        
        for (File file : files) {
            if (file.isDirectory()) {
                String result = findJavaFileName(file, className);
                if (!result.equals(className + ".java")) {
                    return result;
                }
            } else if (file.isFile() && file.getName().endsWith(".java")) {
                // Vérifier si ce fichier contient la déclaration de la classe
                if (fileContainsClass(file, className)) {
                    return file.getName();
                }
            }
        }
        
        return className + ".java";
    }
    
    /**
     * Vérifie si un fichier .java contient la déclaration de la classe
     */
    private static boolean fileContainsClass(File javaFile, String className) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(javaFile.toPath()));
            // Rechercher la déclaration de classe
            return content.matches(".*\\bclass\\s+" + className + "\\b.*") ||
                   content.matches(".*\\bpublic\\s+class\\s+" + className + "\\b.*");
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Obtient le chemin URL du contrôleur.
     * Si l'annotation a une valeur, elle est utilisée.
     * Sinon, le nom de la classe est utilisé (sans "Controller" et en minuscules).
     */
    private static String getControllerPath(Class<?> clazz, ControllerAnnotation annotation) {
        String value = annotation.value();
        if (!value.isEmpty()) {
            // Enlever le / initial si présent
            if (value.startsWith("/")) {
                return value.substring(1);
            }
            return value;
        }
        
        // Générer automatiquement le chemin à partir du nom de la classe
        String className = clazz.getSimpleName();
        
        // Enlever "Controller" à la fin si présent
        if (className.endsWith("Controller")) {
            className = className.substring(0, className.length() - "Controller".length());
        }
        
        // Convertir en kebab-case (SimpleController -> simple-controller)
        String kebabCase = convertToKebabCase(className);
        return kebabCase;
    }
    
    /**
     * Convertit un nom de classe en kebab-case.
     * Exemple: SimpleControllerWithoutClassAnnotation -> simple-controller-without-class-annotation
     */
    private static String convertToKebabCase(String className) {
        if (className.isEmpty()) {
            return className;
        }
        
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(className.charAt(0)));
        
        for (int i = 1; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('-');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    public static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        
        if (resource == null) {
            System.err.println("Package non trouve: " + packageName);
            return new ArrayList<>();
        }
        
        String protocol = resource.getProtocol();
        
        if ("file".equals(protocol)) {
            File directory = new File(resource.getFile());
            return findClasses(directory, packageName);
        } else if ("jar".equals(protocol)) {
            System.err.println("Scanner JAR non implemente dans cette version");
            return new ArrayList<>();
        } else {
            System.err.println("Protocole non supporte: " + protocol);
            return new ArrayList<>();
        }
    }
    
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
}