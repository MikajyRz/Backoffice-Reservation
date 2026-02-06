package com.utils;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.annotations.Authorized;
import com.annotations.Role;
import com.interfaces.SessionUserProvider;

import jakarta.servlet.http.HttpServletRequest;

public class AuthManager {
    private static final String AUTH_VAR = PropertiesUtil.get("auth.variable");

    public static String isAuthorized(Method method, HttpServletRequest req) {
        if (!method.isAnnotationPresent(Authorized.class) && !method.isAnnotationPresent(Role.class)) {
            return null;
        }

        Object userAttr = null;
        if (AUTH_VAR != null) {
            userAttr = req.getSession().getAttribute(AUTH_VAR);
        }

        if (method.isAnnotationPresent(Authorized.class)) {
            if (userAttr == null) {
                return "Non autorise";
            }

            if (userAttr instanceof SessionUserProvider) {
                if (!((SessionUserProvider) userAttr).getAuth()) {
                    return "Non autorise";
                }
            }
        }

        if (method.isAnnotationPresent(Role.class)) {
            Role roleAnnotation = method.getAnnotation(Role.class);
            String required = roleAnnotation.value();
            if (required == null || required.trim().isEmpty()) {
                return null;
            }

            String[] requiredRoles = Arrays.stream(required.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            if (userAttr == null) {
                return "Role non autorise";
            }

            if (userAttr instanceof SessionUserProvider) {
                String[] userRoles = ((SessionUserProvider) userAttr).getRole();
                if (userRoles != null) {
                    for (String r : requiredRoles) {
                        for (String ur : userRoles) {
                            if (ur != null && ur.trim().equals(r)) {
                                return null;
                            }
                        }
                    }
                }
            }

            return "Role non autorise";
        }

        return null;
    }
}
