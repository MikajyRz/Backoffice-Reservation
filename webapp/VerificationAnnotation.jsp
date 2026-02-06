<%@ page import="java.lang.reflect.*" %>
<%@ page import="java.util.*" %>
<%@ page import="test.java.ControllerAnnotationScanner" %>
<%@ page import="com.annotations.ControllerAnnotation" %>
<%@ page import="com.annotations.HandleUrl" %>

<html>
<head>
    <title>Vérification des annotations @HandleUrl</title>
</head>
<body>
    <h2>URLs trouvées dans les contrôleurs du package <code>test.java</code></h2>

    <ul>
    <%
        try {
            String packageName = "test.java";
            List<Class<?>> classes = ControllerAnnotationScanner.getClasses(packageName);

            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(ControllerAnnotation.class)) {
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method m : methods) {
                        if (m.isAnnotationPresent(HandleUrl.class)) {
                            HandleUrl ann = m.getAnnotation(HandleUrl.class);
    %>
                            <li>
                                Classe : <strong><%= clazz.getSimpleName() %></strong>
                                &nbsp;–&nbsp; Methode : <strong><%= m.getName() %></strong>
                                &nbsp;–&nbsp; URL : <strong><%= ann.value() %></strong>
                            </li>
    <%
                        }
                    }
                }
            }
        } catch (Exception e) {
    %>
            <li>Erreur lors du scan des annotations : <%= e.getMessage() %></li>
    <%
        }
    %>
    </ul>
</body>
</html>
