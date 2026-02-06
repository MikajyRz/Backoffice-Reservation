package com.annotations;

import java.lang.annotation.*;

// Annotation de méthode pour une route HTTP GET spécifique
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String value() default "";
}
