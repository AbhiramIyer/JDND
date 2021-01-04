package com.abhiram.microservices.vehiclepricingservice.repository;

import com.abhiram.microservices.vehiclepricingservice.entity.Price;
import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<Price, Long> {
}
