package com.utils;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.exceptions.HttpException;

public class ErrorHandler {
    public static void handle(HttpServletRequest req, HttpServletResponse res, Exception ex) throws IOException {
        if (ex instanceof HttpException httpEx) {
            handleHttpException(req, res, httpEx);
        } else {
            handleHttpException(req, res, new HttpException(500, "Erreur interne du serveur"));
        }
    }

    private static void handleHttpException(HttpServletRequest req, HttpServletResponse res, HttpException ex) throws IOException {
        res.setStatus(ex.getStatus());
        boolean isApi = Boolean.TRUE.equals(req.getAttribute("isApi"));
        if (isApi) {
            ApiResponse apiResponse = new ApiResponse("error", ex.getStatus(), 0, ex.getMessage());
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(JsonSerializer.toJson(apiResponse));
        } else {
            res.setContentType("text/html;charset=UTF-8");
            res.getWriter().println("""
                <html>
                    <head><title>Erreur %d</title></head>
                    <body>
                        <h1>Erreur %d</h1>
                        <p>%s</p>
                    </body>
                </html>
                """.formatted(ex.getStatus(), ex.getStatus(), ex.getMessage()));
        }
    }
}
