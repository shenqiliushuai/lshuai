package com.les.ls.utils;


public class Test {
    public static void main(String[] args) {
        int a = -4;
        System.out.println("value->[" + a + "]binary->[" + Integer.toBinaryString(a) + "]");
        a >>= 2;
        System.out.println("value->[" + a + "]binary->[" + Integer.toBinaryString(a) + "]");
        a <<= 8;
        System.out.println("value->[" + a + "]binary->[" + Integer.toBinaryString(a) + "]");
    }

}
