package com.udacity.pricing;

import com.udacity.pricing.entity.Price;
import org.apache.catalina.LifecycleState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PricingServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void getAllPrices() {
        ResponseEntity<Object> response = testRestTemplate.getForEntity("http://localhost:" + port + "/prices/", Object.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void getPriceOfVehicleId1() {
        ResponseEntity<Price> response = testRestTemplate.getForEntity("http://localhost:" + port + "/prices/1", Price.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

}
