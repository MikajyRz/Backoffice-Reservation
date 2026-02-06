package test.java;

import com.annotations.ControllerAnnotation;
import com.annotations.HandleUrl;
import com.classes.ModelView;

@ControllerAnnotation("/controller2")
public class TestControllerAnnotation2 {
    
    @HandleUrl("/testmodelview")
    public ModelView method1() {
        ModelView mvw = new ModelView();
        mvw.setView("user.jsp");
        mvw.addData("nom", "Razafimanjato");
        mvw.addData("age", 21);
        return mvw;
    }

    @HandleUrl("/teststring")
    public String fonctionner() {
        return "C'est un test de string";
    }

    @HandleUrl("/test/{id}")
    public String fonctionnerid() {
        return "C'est un test id";
    }
}

