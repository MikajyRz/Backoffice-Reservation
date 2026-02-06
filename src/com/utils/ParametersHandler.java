package com.utils;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.annotations.Param;
import com.annotations.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

// Utilitaire qui convertit une valeur String provenant de la requête
// vers le type Java attendu par le paramètre de la méthode (int, double, ...)
public class ParametersHandler {

    // Convertit la chaîne "value" vers le type "targetType" (primitifs + String)
    public static Object convertToType(String value, Class<?> targetType) {
        if (value == null) {
            return getDefaultValue(targetType);
        }

        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return targetType.equals(int.class) ? 0 : null;
            }
        } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return targetType.equals(long.class) ? 0L : null;
            }
        } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return targetType.equals(double.class) ? 0.0d : null;
            }
        } else if (targetType.equals(float.class) || targetType.equals(Float.class)) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return targetType.equals(float.class) ? 0.0f : null;
            }
        } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (targetType.equals(byte.class) || targetType.equals(Byte.class)) {
            try {
                return Byte.parseByte(value);
            } catch (NumberFormatException e) {
                return targetType.equals(byte.class) ? (byte) 0 : null;
            }
        } else if (targetType.equals(short.class) || targetType.equals(Short.class)) {
            try {
                return Short.parseShort(value);
            } catch (NumberFormatException e) {
                return targetType.equals(short.class) ? (short) 0 : null;
            }
        } else if (targetType.equals(char.class) || targetType.equals(Character.class)) {
            return (value.isEmpty()) ? (targetType.equals(char.class) ? '\u0000' : null) : value.charAt(0);
        } else if (targetType.equals(Date.class)) {
            return parseDate(value);
        } else if (targetType.equals(LocalDate.class)) {
            return parseLocalDate(value);
        } else if (targetType.equals(LocalDateTime.class)) {
            return parseLocalDateTime(value);
        }

        // Par défaut, on renvoie la valeur brute (String) si aucun type n'est géré explicitement
        return value;
    }

    private static Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String[] dateFormats = {
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "yyyy-MM-dd HH:mm:ss",
            "dd-MM-yyyy",
            "yyyy/MM/dd"
        };

        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.parse(value);
            } catch (ParseException e) {
                // on essaie le format suivant
            }
        }

        return null;
    }

    private static LocalDate parseLocalDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e1) {
            String[] patterns = {"dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy"};

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDate.parse(value, formatter);
                } catch (DateTimeParseException e2) {
                    // on essaie le pattern suivant
                }
            }
        }

        return null;
    }

    private static LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e1) {
            String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy HH:mm:ss",
                "MM/dd/yyyy HH:mm:ss"
            };

            for (String pattern : patterns) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDateTime.parse(value, formatter);
                } catch (DateTimeParseException e2) {
                    // on essaie le pattern suivant
                }
            }
        }

        return null;
    }

    // Valeurs par défaut pour les types primitifs quand la valeur est absente (null)
    private static Object getDefaultValue(Class<?> targetType) {
        if (targetType.equals(int.class)) return 0;
        if (targetType.equals(long.class)) return 0L;
        if (targetType.equals(double.class)) return 0.0d;
        if (targetType.equals(float.class)) return 0.0f;
        if (targetType.equals(boolean.class)) return false;
        if (targetType.equals(byte.class)) return (byte) 0;
        if (targetType.equals(short.class)) return (short) 0;
        if (targetType.equals(char.class)) return '\u0000';
        return null;
    }

    public static Object[] prepareMethodParameters(HttpServletRequest req, Method method) {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] paramValues = new Object[parameters.length];

            boolean multipart = req.getContentType() != null
                && req.getContentType().toLowerCase().startsWith("multipart/");

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];

                if (param.isAnnotationPresent(Session.class)) {
                    if (Map.class.isAssignableFrom(param.getType())) {
                        Type genericType = param.getParameterizedType();
                        if (genericType instanceof ParameterizedType pt) {
                            Type[] types = pt.getActualTypeArguments();
                            if (types.length == 2
                                && types[0] == String.class
                                && types[1] == Object.class) {
                                paramValues[i] = new SessionMap(req.getSession());
                                continue;
                            }
                        }
                    }
                    throw new RuntimeException(
                        "@Session doit être utilisé sur un paramètre de type Map<String, Object>"
                    );
                }

                if (Map.class.isAssignableFrom(param.getType())
                    && param.getParameterizedType() instanceof ParameterizedType pType) {
                    Type[] typeArgs = pType.getActualTypeArguments();
                    if (typeArgs.length == 2
                        && typeArgs[0] == String.class
                        && typeArgs[1] instanceof Class
                        && ((Class<?>) typeArgs[1]).equals(byte[].class)) {
                        Map<String, byte[]> filesMap = new HashMap<>();
                        if (multipart) {
                            for (Part part : req.getParts()) {
                                if (part.getContentType() == null) {
                                    continue;
                                }
                                String submittedName = part.getSubmittedFileName();
                                if (submittedName == null || part.getSize() <= 0) {
                                    continue;
                                }
                                byte[] data = part.getInputStream().readAllBytes();
                                String key = submittedName;
                                int index = 0;
                                while (filesMap.containsKey(key)) {
                                    key = submittedName + index;
                                    index++;
                                }
                                filesMap.put(key, data);
                            }
                        }
                        paramValues[i] = filesMap;
                        continue;
                    }
                }

                if (Map.class.isAssignableFrom(param.getType())) {
                    Map<String, Object> mapParam = new HashMap<>();
                    Map<String, String[]> requestParams = req.getParameterMap();
                    for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                        String key = entry.getKey();
                        String[] values = entry.getValue();
                        if (values != null && values.length == 1) {
                            mapParam.put(key, values[0]);
                        } else {
                            mapParam.put(key, values);
                        }
                    }
                    paramValues[i] = mapParam;
                    continue;
                }

                String paramName;
                if (param.isAnnotationPresent(Param.class)) {
                    paramName = param.getAnnotation(Param.class).value();
                } else {
                    paramName = param.getName();
                }

                String value = req.getParameter(paramName);
                if (value == null) {
                    Object attr = req.getAttribute(paramName);
                    if (attr != null) {
                        value = attr.toString();
                    }
                }

                if (ObjectBinder.isSimpleType(param.getType())) {
                    paramValues[i] = convertToType(value, param.getType());
                } else {
                    paramValues[i] = ObjectBinder.bind(param, req);
                }
            }

            return paramValues;
        } catch (Exception e) {
            throw new RuntimeException("Error preparing method parameters", e);
        }
    }
}
