package com.vnnet.kpi.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.vnnet.kpi"})
public class KpiBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(KpiBatchApplication.class, args);
    }
}
