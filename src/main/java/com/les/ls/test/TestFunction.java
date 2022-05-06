package com.les.ls.test;

import java.util.function.Function;

public class TestFunction {

    static Function<String, String> function1 = (s) -> {
        System.out.println("exec 1");
        return s + "function1";
    };

    static Function<String, String> function2 = (s) -> {
        System.out.println("exec 2");
        return s + "function2";
    };


    public static void main(String[] args) {

        System.out.println(function1.compose(function2).apply("test"));

        System.out.println(function1.andThen(function2).apply("test"));

        System.out.println(Function.identity().compose(function1).apply("test"));
        //循环调用 导致不可用
        //System.out.println(Function.identity().andThen(function1).apply("test"));
    }

}
