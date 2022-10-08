package com.les.ls;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@MapperScan("com.les.ls.dao")
@ComponentScan(basePackages = {"com.les.shengkai", "com.les.ls", "com.les.yjhui"})
//@SpringBootApplication()
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LsApplication {


    @Resource
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(LsApplication.class, args);
    }

}
