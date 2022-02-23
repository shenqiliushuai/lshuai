package com.les.yjhui.designMode.observerMode8;
import java.util.Observable;

public class thief extends Observable {
    public void makeChanged(Message message) {
        System.out.println(message.getMessage()+"  我是"+message.getFrom());
        setChanged();
        notifyObservers(message);
    }
}
