<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head><title>Resource Not Found</title></head>
    <body style="font-family:sans-serif;">
        <p>Aucun fichier ni url correspondant n'a ete trouvee.</p>
        <p>URL demandee : <strong>${requestScope['jakarta.servlet.error.request_uri']}</strong></p>
    </body>
</html>
