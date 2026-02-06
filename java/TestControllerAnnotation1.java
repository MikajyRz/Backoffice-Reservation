package test.java;

import com.annotations.ControllerAnnotation;
import com.annotations.HandleUrl;

@ControllerAnnotation("/controller1")
public class TestControllerAnnotation1 {
    
    @HandleUrl("/method1")
    public String method1() {
        return "salut";
    }
    
    @HandleUrl("/testnombre")
    public int liste() {
        return 154;
    }
}

