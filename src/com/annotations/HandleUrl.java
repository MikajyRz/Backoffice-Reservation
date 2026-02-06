package com.annotations;

import java.lang.annotation.*;

// Annotation de méthode pour associer une URL à une méthode de contrôleur
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleUrl {
    String value() default "";
}
