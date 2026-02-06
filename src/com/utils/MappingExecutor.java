package com.utils;

import java.lang.reflect.Method;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.classes.ModelView;
import com.annotations.Api;
import com.exceptions.*;
import com.exceptions.HttpException;

public class MappingExecutor {

    public static void execute(HttpServletRequest req, HttpServletResponse res, MappingHandler mapH) throws Exception {
        Method method = mapH.getMethode();

        if (method.isAnnotationPresent(Api.class)) {
            req.setAttribute("isApi", true);
        }

        Object instance = mapH.getClasse().getDeclaredConstructor().newInstance();

        String authMsg = AuthManager.isAuthorized(method, req);
        if (authMsg != null) {
            throw new HttpException(401, authMsg);
        }

        Object[] methodParams = ParametersHandler.prepareMethodParameters(req, method);

        Object result = method.invoke(instance, methodParams);

        if (method.isAnnotationPresent(Api.class)) {
            ApiResponse apiResponse = ApiResponse.from(result);

            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");

            String json = JsonSerializer.toJson(apiResponse);

            res.getWriter().write(json);
            return;
        }

        if (result instanceof String) {
            res.getWriter().println(result);
        } else if (result instanceof ModelView mv) {

            if (mv.getData() != null) {
                for (Map.Entry<String, Object> e : mv.getData().entrySet()) {
                    req.setAttribute(e.getKey(), e.getValue());
                }
            }

            String viewPath = mv.getView();
            if (!viewPath.startsWith("/"))
                viewPath = "/" + viewPath;

            try {
                RequestDispatcher dispatcher = req.getRequestDispatcher(viewPath);

                if (dispatcher == null) {
                    throw new NotFoundException("Vue introuvable: " + viewPath);
                }

                dispatcher.forward(req, res);

            } catch (Exception ex) {
                ErrorHandler.handle(req, res, ex);
            }
        } else {
            res.getWriter().println("Type de retour non géré");
        }
    }
}
