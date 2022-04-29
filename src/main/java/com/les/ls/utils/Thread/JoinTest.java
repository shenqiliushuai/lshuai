package com.les.ls.utils.Thread;

import java.util.concurrent.TimeUnit;

public class JoinTest {

    public static void main(String[] args) throws Exception {
        testJoin();
    }

    static void testJoin() {
        Thread t1 = new Thread(()->{
            for(int i=0; i<5; i++) {
                System.out.println("A" + i);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(()->{
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=0; i<5; i++) {
                System.out.println("B" + i);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t2.start();
        t1.start();
    }
}
