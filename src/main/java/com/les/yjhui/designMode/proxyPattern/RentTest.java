package com.les.yjhui.designMode.proxyPattern;

// 租房子测试
// 其实这就类似中介公司来了一个租房子的人要租房子
public class RentTest {
    public static void main(String[] args) {
        // 被代理类：房东Jack
        HostBill bill = new HostBill(3000);
        // 创建代理类对象（假设租户这时要看Jack的房子，就需要Jack的代理）
        ProxyCompanyForBill forBill = new ProxyCompanyForBill(bill);
        // 调用出租房子的方法
        // 表面上看的是调用的中介公司的rent方式，实际则是通过中介公司调用的Jack房东的rent方法
        forBill.rent();
    }
}

