package com.les.ls.dispatcher;

import com.les.ls.handler.TestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dispatcher")
public class TestDispatcher {
    @Resource
    private CustomDispatcher<TestParam, String> customDispatcher;

    @GetMapping
    public String test() {
        TestParam testParam = new TestParam();
        testParam.setKey("1");
        testParam.setValue("2");
        System.out.println(customDispatcher.dispatch(testParam));
        testParam.setKey("test");
        testParam.setValue("TEST1");
        return customDispatcher.dispatch(testParam);
    }
}
