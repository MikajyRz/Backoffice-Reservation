package com.annotations;

import java.lang.annotation.*;

// Annotation de classe pour marquer une classe comme contrôleur du framework
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ControllerAnnotation {
    String value() default "";
}
