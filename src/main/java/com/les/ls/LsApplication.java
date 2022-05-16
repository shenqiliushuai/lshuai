package com.les.ls;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@MapperScan("com.les.ls.dao")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ComponentScans(value = {@ComponentScan("com.les.shengkai")})
public class LsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LsApplication.class, args);
    }

}
