package com.les.yjhui.designMode.observerMode;


import java.util.Observable;
import java.util.Observer;

public class police implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        Message arg1 = (Message) arg;
        System.out.println("监控到:小偷" + arg1.getFrom() + "与小偷" + arg1.getTo() + "进行了通讯");
        System.out.println("通讯内容是:" + arg1.getMessage());
    }
}
