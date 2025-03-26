package com.group2.glamping;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

@SpringBootApplication
public class GlampingApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    public static void main(String[] args) {
        System.out.println("LocalDateTime: " + LocalDateTime.now());
        System.out.println("ZonedDateTime: " + ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        SpringApplication.run(GlampingApplication.class, args);
    }


}