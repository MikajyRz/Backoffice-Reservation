package test.java;

import com.annotations.HandleUrl;

public class TestAnnotationUrl {
    @HandleUrl("/TestHandleUrl")
    public void home() {}

    @HandleUrl("/test/home")
    public void test() {}
}
