package com.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.*;
import java.util.*;

public class ObjectBinder {

    // =====================================================
    // POINT D’ENTRÉE
    // =====================================================
    public static Object bind(Parameter parameter, HttpServletRequest req)
            throws Exception {

        String rootName = parameter.getName();
        return bindClass(parameter.getType(), rootName, req);
    }

    // =====================================================
    // BIND RECURSIF D’OBJETS
    // =====================================================
    private static Object bindClass(Class<?> clazz,
                                    String prefix,
                                    HttpServletRequest req) throws Exception {

        Object obj = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            String paramKey = prefix + "." + field.getName();

            // ============================
            // TYPE SIMPLE
            // ============================
            if (isSimpleType(fieldType)) {
                String val = req.getParameter(paramKey);
                if (val != null) {
                    Object converted =
                        ParametersHandler.convertToType(val, fieldType);
                    field.set(obj, converted);
                }
                continue;
            }

            // ============================
            // LIST<T>
            // ============================
            if (List.class.isAssignableFrom(fieldType)) {
                bindList(obj, field, paramKey, req);
                continue;
            }

            // ============================
            // TABLEAU T[]
            // ============================
            if (fieldType.isArray()) {
                bindArray(obj, field, paramKey, req);
                continue;
            }

            // ============================
            // MAP<K,V>
            // ============================
            if (Map.class.isAssignableFrom(fieldType)) {
                bindMap(obj, field, paramKey, req);
                continue;
            }

            // ============================
            // OBJET COMPLEXE (récursion)
            // ============================
            boolean exists =
                req.getParameterMap().keySet()
                    .stream()
                    .anyMatch(k -> k.startsWith(paramKey + "."));

            if (exists) {
                Object nested =
                    bindClass(fieldType, paramKey, req);
                field.set(obj, nested);
            }
        }

        return obj;
    }

    // =====================================================
    // LIST<T>
    // =====================================================
    private static void bindList(Object parent,
                                 Field field,
                                 String prefix,
                                 HttpServletRequest req) throws Exception {

        ParameterizedType listType =
            (ParameterizedType) field.getGenericType();

        Class<?> itemClass =
            (Class<?>) listType.getActualTypeArguments()[0];

        List<Object> results = new ArrayList<>();
        int index = 0;

        while (true) {

            String elementPrefix = prefix + "[" + index + "]";

            boolean exists =
                req.getParameterMap().keySet()
                    .stream()
                    .anyMatch(p -> p.startsWith(elementPrefix + "."));

            if (!exists) break;

            Object item =
                bindClass(itemClass, elementPrefix, req);

            results.add(item);
            index++;
        }

        if (!results.isEmpty()) {
            field.set(parent, results);
        }
    }

    // =====================================================
    // TABLEAU T[]
    // =====================================================
    private static void bindArray(Object parent,
                                  Field field,
                                  String prefix,
                                  HttpServletRequest req) throws Exception {

        Class<?> componentType =
            field.getType().getComponentType();

        List<Object> buffer = new ArrayList<>();
        int index = 0;

        while (true) {

            String elementPrefix = prefix + "[" + index + "]";

            boolean exists =
                req.getParameterMap().keySet()
                    .stream()
                    .anyMatch(p -> p.startsWith(elementPrefix + "."));

            if (!exists) break;

            Object item =
                bindClass(componentType, elementPrefix, req);

            buffer.add(item);
            index++;
        }

        if (!buffer.isEmpty()) {
            Object array =
                Array.newInstance(componentType, buffer.size());

            for (int i = 0; i < buffer.size(); i++) {
                Array.set(array, i, buffer.get(i));
            }

            field.set(parent, array);
        }
    }

    // =====================================================
    // MAP<K,V>  (clé String)
    // =====================================================
    private static void bindMap(Object parent,
                                Field field,
                                String prefix,
                                HttpServletRequest req) throws Exception {

        ParameterizedType mapType =
            (ParameterizedType) field.getGenericType();

        Class<?> valueType =
            (Class<?>) mapType.getActualTypeArguments()[1];

        Map<String, Object> result = new HashMap<>();
        Set<String> keys = new HashSet<>();

        // Extraire les clés : map[key].field
        for (String param : req.getParameterMap().keySet()) {

            if (param.startsWith(prefix + "[")) {

                String temp = param.substring(prefix.length());
                int end = temp.indexOf("]");

                if (end > 0) {
                    String key = temp.substring(1, end);
                    keys.add(key);
                }
            }
        }

        for (String key : keys) {

            Object value =
                bindClass(valueType,
                          prefix + "[" + key + "]",
                          req);

            result.put(key, value);
        }

        if (!result.isEmpty()) {
            field.set(parent, result);
        }
    }

    // =====================================================
    // TYPES SIMPLES
    // =====================================================
    public static boolean isSimpleType(Class<?> type) {

        return type.isPrimitive()
            || type == String.class
            || Number.class.isAssignableFrom(type)
            || type == Boolean.class
            || type == Date.class
            || type.getName().startsWith("java.time");
    }
}
