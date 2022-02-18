package com.les.yjhui.designMode.observerMode;


public class RealTest {
    public static void main(String[] args) {
        police observer = new police();
        thief realSubject = new thief();
        Message message = new Message("土豆土豆，我是地瓜", "A", "B");
        realSubject.addObserver(observer);
        realSubject.makeChanged(message);
    }
}
