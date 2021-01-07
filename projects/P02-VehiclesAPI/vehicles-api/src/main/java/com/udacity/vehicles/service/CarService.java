package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.Address;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final WebClient pricingServiceClient;
    private final WebClient mapsClient;

    public CarService(CarRepository repository, @Qualifier("pricing") WebClient pricingServiceClient, @Qualifier("maps") WebClient mapsClient) {
        this.repository = repository;
        this.pricingServiceClient = pricingServiceClient;
        this.mapsClient = mapsClient;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll().stream().map(car -> {
            setPrice(car);
            setAddress(car);
            return car;
        }).collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);

        setPrice(car);
        setAddress(car);

        return car;
    }

    private void setPrice(Car car) {
        Long id = car.getId();
        Price p = getPrice(id);
        if (p != null) {
            car.setPrice(p.getPrice().toString());
        }
    }

    private Price getPrice(Long id) {
        return pricingServiceClient.get().uri("/prices/" + id).retrieve().bodyToMono(Price.class).block();
    }

    private void setAddress(Car car) {
        Address a = getAddress(car.getLocation().getLat(), car.getLocation().getLon());
        if (a != null) {
            car.getLocation().setAddress(a.getAddress());
        }
    }

    private Address getAddress(Double lat, Double lon) {
        return mapsClient.get()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/maps").queryParam("lat", lat).queryParam("lon", lon).build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Address.class)
                .block();
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.getDetails().setManufacturer(car.getDetails().getManufacturer());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}
