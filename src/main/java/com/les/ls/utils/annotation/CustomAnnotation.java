package com.les.ls.utils.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomAnnotation {
    String name();

    enum Test {TEST1, TEST2}

    Test test();
}
