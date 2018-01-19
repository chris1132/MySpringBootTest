package com.boot2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by wangchaohui on 2018/1/19.
 */
//exclude = DataSourceAutoConfiguration.class 将spring boot自带的DataSourceAutoConfiguration禁掉，因为它会默认读取application文件
@SpringBootApplication
@EnableScheduling
public class ApplicationTest implements HealthIndicator {

    @Override
    public Health health(){
        return Health.up().withDetail("hello","wang").build();
    }

    public static void main(String[] args){
        SpringApplication.run(ApplicationTest.class,args);
    }
}
