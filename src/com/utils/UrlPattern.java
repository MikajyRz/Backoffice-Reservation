package com.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Représente un pattern d'URL de type /user/{id} et permet
// 1) de tester si une URL correspond
// 2) d'extraire les valeurs des paramètres (id, ...)
public class UrlPattern {
    private String originalPattern;
    private Pattern regex;
    private List<String> paramNames;

    // Construit un pattern à partir d'une chaîne de type /user/{id}
    public UrlPattern(String pattern) {
        this.originalPattern = pattern;
        this.paramNames = new java.util.ArrayList<>();
        this.regex = compilePattern(pattern);
    }

    // Transforme le pattern fonctionnel (/user/{id}) en expression régulière compilée
    private Pattern compilePattern(String pattern) {
        StringBuilder regexBuilder = new StringBuilder("^");
        int lastIndex = 0;

        Pattern paramPattern = Pattern.compile("\\{([^/]+)\\}");
        Matcher matcher = paramPattern.matcher(pattern);

        while (matcher.find()) {
            regexBuilder.append(Pattern.quote(pattern.substring(lastIndex, matcher.start())));
            regexBuilder.append("([^/]+)");
            paramNames.add(matcher.group(1));
            lastIndex = matcher.end();
        }

        regexBuilder.append(Pattern.quote(pattern.substring(lastIndex)));
        regexBuilder.append("$");

        return Pattern.compile(regexBuilder.toString());
    }

    // Indique si une URL concrète (ex: /user/12) correspond à ce pattern
    public boolean matches(String url) {
        return regex.matcher(url).matches();
    }

    // Extrait les paramètres de l'URL (ex: id=12) sous forme de Map<String,String>
    public Map<String, String> extractParams(String url) {
        Map<String, String> params = new HashMap<>();
        Matcher matcher = regex.matcher(url);

        if (matcher.matches()) {
            for (int i = 0; i < paramNames.size(); i++) {
                params.put(paramNames.get(i), matcher.group(i + 1));
            }
        }

        return params;
    }

    public String getOriginalPattern() {
        return originalPattern;
    }
}
