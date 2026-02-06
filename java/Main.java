package test.java;

import java.lang.reflect.Method;
import com.annotations.HandleUrl;


public class Main {
    public static void main(String[] args) {
        Class<?> clazz = TestAnnotationUrl.class;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(HandleUrl.class)) {
                HandleUrl ann = m.getAnnotation(HandleUrl.class);
                System.out.println(ann.value());
            }
        }
    }
}