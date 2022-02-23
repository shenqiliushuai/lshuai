package com.les.yjhui.designMode.proxyPattern;

// 一个名为Bill的房东要出租房子
public class HostBill implements RentHouse {
    private double housingPrice;

    public HostBill() {
    }

    public double getHousingPrice() {
        return housingPrice;
    }

    public HostBill(double housingPrice) {
        this.housingPrice = housingPrice;
    }

    @Override
    public void rent() {
        System.out.println("我Bill以3000/月的价格要出租房子！！！");
    }
}

