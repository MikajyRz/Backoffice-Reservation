package com.annotations;

import java.lang.annotation.*;

// Annotation de méthode pour une route HTTP POST spécifique
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostMapping {
    String value() default "";
}
