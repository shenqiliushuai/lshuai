package com.les.yjhui.designMode.proxyPattern;

// 代理类和被代理类都有的行为
// 被代理类：各种房东要出租房子
// 代理类：中介公司帮助房东出租房子，也相当于有出租房子这个行为
public interface RentHouse {
    // 出租房子方法
    void rent();
}

