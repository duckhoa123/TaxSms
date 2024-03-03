package com.vnnet.kpi.readpdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.vnnet.kpi"})
public class KpiReadpdfUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KpiReadpdfUiApplication.class, args);
    }

}
