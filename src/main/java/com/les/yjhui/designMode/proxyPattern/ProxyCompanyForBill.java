package com.les.yjhui.designMode.proxyPattern;

// 中介公司为Bill服务
public class ProxyCompanyForBill implements RentHouse {
    // 同样声明被代理类对象（出租房子的人Bill）
    HostBill bill;
    private double commission;
    // 设置传入的房东bill
    public void setBill(HostBill bill) {
        this.bill = bill;
    }

    public ProxyCompanyForBill(HostBill bill) {
        this.bill = bill;
        this.commission = bill.getHousingPrice() * 0.2;
    }
    public ProxyCompanyForBill() {}

    @Override
    public void rent() {
        // 调用被代理类（出租房子的人bill）的rent方法
        bill.rent();
        System.out.println("房屋中介抽取两成中介费:" + this.commission +"元");
    }
}
