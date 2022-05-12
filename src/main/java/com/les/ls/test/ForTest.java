package com.les.ls.test;

public class ForTest {
    public static void main(String[] args) {
        forTest();
    }

    private static void forTest() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                flag:
                for (int k = 0; k < 10; k++) {
                    for (int b = 0; b < 10; b++) {
                        if (b == i) {
                            break flag;
                        }
                    }
                }
            }
        }
    }
}
