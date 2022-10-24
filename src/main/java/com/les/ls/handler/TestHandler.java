package com.les.ls.handler;

import com.les.ls.utils.annotation.CustomAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@CustomAnnotation(name = "test", test = CustomAnnotation.Test.TEST1)
public class TestHandler implements CustomHandler<TestParam, String> {

    @Override
    public String test(TestParam testParam) {
        return "test complete !";
    }
}
