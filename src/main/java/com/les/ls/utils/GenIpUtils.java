package com.les.ls.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;

import java.io.FileInputStream;
import java.net.InetAddress;

public final class GenIpUtils {
    public static void main(String[] args) throws Exception{
        String url = "C:\\Users\\les.liu\\Desktop\\个人资料\\GeoLite2-City.mmdb";

        FileInputStream database = new FileInputStream(url);

        DatabaseReader reader = new DatabaseReader.Builder(database).build();

        String ip = "183.47.51.33";

        InetAddress ipAddress = InetAddress.getByName(ip);
        CountryResponse countryResponse = reader.country(ipAddress);
        CityResponse cityResponse = reader.city(ipAddress);

        Country country = countryResponse.getCountry();
        String name = country.getNames().get("zh-CN");
        System.out.println(name);

        City city = cityResponse.getCity();
        name = city.getNames().get("zh-CN");
        System.out.println(name);
    }


}
