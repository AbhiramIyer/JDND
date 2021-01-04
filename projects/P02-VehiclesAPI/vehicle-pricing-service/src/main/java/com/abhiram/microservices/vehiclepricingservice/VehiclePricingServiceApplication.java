package com.abhiram.microservices.vehiclepricingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class VehiclePricingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehiclePricingServiceApplication.class, args);
    }

}
