package com.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.io.File;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;

import com.annotations.Param;
import com.annotations.Session;
import com.utils.*;
import com.classes.ModelView;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    private RequestDispatcher defaultDispatcher;
    private static final List<String> INDEX_FILES = Arrays.asList(
        "/index.html", "/index.htm", "/index.jsp"
    );

    // Initialisation du servlet frontal : scan des contrôleurs et enregistrement des mappings
    @Override
    public void init() throws ServletException {
        defaultDispatcher = getServletContext().getNamedDispatcher("default");
        try {
            ServletContext ctx = getServletContext();

            Properties config = new Properties();
            InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("framework.properties");
            if (is != null) {
                config.load(is);
            }
            ctx.setAttribute("frameworkConfig", config);

            String uploadDir = config.getProperty("framework.upload.dir", "uploads");
            String uploadBase = ctx.getRealPath("/") + File.separator + uploadDir;
            FileStorage.init(uploadBase);

            String scanPackage = config.getProperty("framework.scan.package", "test.java");
            Map<String, List<MappingHandler>> urlMappings = ScanningUrl.scanUrlMappings(scanPackage);
            System.out.println("=== URL MAPPINGS ===");
            for (String k : urlMappings.keySet()) {
                System.out.println(" - " + k);
            }
            ctx.setAttribute("urlMappings", urlMappings);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // Méthode centrale qui intercepte toutes les requêtes HTTP et décide du traitement
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {

            String path = req.getRequestURI().substring(req.getContextPath().length());
            System.out.println("[FrontServlet] path recu = " + path);

            @SuppressWarnings("unchecked")
            Map<String, List<MappingHandler>> urlMappings = (Map<String, List<MappingHandler>>) getServletContext().getAttribute("urlMappings");

            if (path.equals("/") || path.isEmpty()) {
                String indexPath = findExistingIndex();
                if (indexPath != null) {
                    req.getRequestDispatcher(indexPath).forward(req, res);
                    return;
                } else {
                    throw new com.exceptions.NotFoundException("Index introuvable");
                }
            }

            List<MappingHandler> mapHList = urlMappings != null ? urlMappings.get(path) : null;
            MappingHandler mapH = null;

            if (mapHList != null) {
                String reqMethod = req.getMethod();
                for (MappingHandler mh : mapHList) {
                    if (mh.getHttpMethod() == null || mh.getHttpMethod().equals("ALL") || mh.getHttpMethod().equalsIgnoreCase(reqMethod)) {
                        mapH = mh;
                        break;
                    }
                }
            }

            // Si aucun mapping exact n'est trouvé, on essaie de faire correspondre un pattern d'URL (ex: /user/{id})
            if (mapH == null && urlMappings != null) {
                mapH = findMatchingPattern(path, urlMappings, req);
            }

            boolean resourceExists = getServletContext().getResource(path) != null;
            if (resourceExists) {
                defaultServe(req, res);
            } else if (mapH != null) {
                handleMapping(req, res, mapH);
            } else {
                throw new com.exceptions.NotFoundException("URL introuvable : " + path);
            }

        } catch (Exception e) {
            Throwable cause = e;
            if (e instanceof InvocationTargetException ite && ite.getCause() != null) {
                cause = ite.getCause();
            }

            try {
                ErrorHandler.handle(req, res, (Exception) cause);
            } catch (IOException ioEx) {
                throw new ServletException(ioEx);
            }
        }
    }

    // Exécute la méthode de contrôleur associée à l'URL et gère son type de retour
    private void handleMapping(HttpServletRequest req, HttpServletResponse res, MappingHandler mapH)
        throws Exception {
        Method method = mapH.getMethode();
        Class<?> returnType = method.getReturnType();
        Object instance = mapH.getClasse().getDeclaredConstructor().newInstance();

        String authMsg = AuthManager.isAuthorized(method, req);
        if (authMsg != null) {
            throw new com.exceptions.HttpException(401, authMsg);
        }

        if (method.isAnnotationPresent(com.annotations.Api.class)) {
            req.setAttribute("isApi", true);
        }

        Object[] preparedParams = prepareMethodParameters(req, method);

        Object result = method.invoke(instance, preparedParams);

        // Mode API JSON si @Api est présent
        if (method.isAnnotationPresent(com.annotations.Api.class)) {
            ApiResponse apiResponse = buildApiResponse(result);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            String json = JsonSerializer.toJson(apiResponse);
            res.getWriter().write(json);
            return;
        }

        // Comportement MVC existant étendu
        if (returnType.equals(void.class)) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else if (returnType.equals(String.class)) {
            res.setContentType("text/plain;charset=UTF-8");
            res.getWriter().println((String) result);
        } else if (returnType.equals(ModelView.class)) {
            ModelView mv = (ModelView) result;
            if (mv.getData() != null) {
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
            }

            String viewPath = mv.getView();
            if (viewPath != null && !viewPath.startsWith("/")) {
                viewPath = "/" + viewPath;
            }
            req.getRequestDispatcher(viewPath).forward(req, res);
        } else if (ObjectBinder.isSimpleType(returnType)) {
            res.setContentType("text/plain;charset=UTF-8");
            res.getWriter().println(String.valueOf(result));
        } else {
            res.setContentType("text/plain;charset=UTF-8");
            res.getWriter().println("Le type de retour n'est pas un String ni un ModelView.");
        }
    }

    private ApiResponse buildApiResponse(Object result) {
        if (result == null) {
            return new ApiResponse("success", 200, 0, null);
        }

        if (result instanceof ModelView mv) {
            int count = mv.getData() != null ? mv.getData().size() : 0;
            return new ApiResponse("success", 200, count, mv.getData());
        }

        if (result instanceof Collection<?> col) {
            return new ApiResponse("success", 200, col.size(), col);
        }

        if (result.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(result);
            return new ApiResponse("success", 200, len, result);
        }

        return new ApiResponse("success", 200, 1, result);
    }

    // Recherche un fichier index par défaut (index.html / index.jsp, ...)
    private String findExistingIndex() {
        for (String index : INDEX_FILES) {
            try {
                if (getServletContext().getResource(index) != null) {
                    return index;
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid path in INDEX_FILES: " + index, e);
            }
        }
        return null;
    }

    // Réponse HTML personnalisée lorsqu'aucune ressource ni mapping n'est trouvée
    private void customServe(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = res.getWriter()) {
            String uri = req.getRequestURI();
            String responseBody = """
                <html>
                    <head><title>Resource Not Found</title></head>
                    <body style=\"font-family:sans-serif;\">
                        <p>Aucun fichier ni url correspondant n'a ete trouve.</p>
                        <p>URL demandée : <strong>%s</strong></p>
                    </body>
                </html>
                """.formatted(uri);
            out.println(responseBody);
        }
    }

    // Délègue le traitement au servlet par défaut du conteneur (fichiers statiques, JSP...)
    private void defaultServe(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (defaultDispatcher != null) {
            defaultDispatcher.forward(req, res);
        } else {
            customServe(req, res);
        }
    }

    // Cherche un mapping dont le pattern d'URL correspond au chemin demandé et extrait les paramètres
    private MappingHandler findMatchingPattern(String path, Map<String, List<MappingHandler>> urlMappings, HttpServletRequest req) {
        for (List<MappingHandler> handlers : urlMappings.values()) {
            for (MappingHandler handler : handlers) {
                if (handler.getUrlPattern() != null) {
                    String pattern = handler.getUrlPattern().getOriginalPattern();
                    boolean matches = handler.getUrlPattern().matches(path);
                    System.out.println("[FrontServlet] test pattern = " + pattern + " contre path = " + path + " => " + matches);
                    if (matches) {
                        Map<String, String> params = handler.getUrlPattern().extractParams(path);
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        return handler;
                    }
                }
            }
        }
        return null;
    }

    // Prépare les paramètres de la méthode de contrôleur à partir de la requête HTTP
    private Object[] prepareMethodParameters(HttpServletRequest req, Method method) {
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
                    Param ann = param.getAnnotation(Param.class);
                    paramName = ann.value();
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
                    paramValues[i] = ParametersHandler.convertToType(value, param.getType());
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