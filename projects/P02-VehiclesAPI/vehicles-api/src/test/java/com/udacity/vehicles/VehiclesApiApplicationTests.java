package com.udacity.vehicles;

import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class VehiclesApiApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void getAllCars() {
        HttpEntity<Car> request = new HttpEntity<>(getCar(), new HttpHeaders());
        ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/cars", request, String.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        response = testRestTemplate.getForEntity("http://localhost:" + port + "/cars/", String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void canAddACarAndFindItAgainById() {
        Car newCar = getCar();
        ResponseEntity<Car> response = doAddCarOperation(newCar);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        //Get the id of the car that was created
        Long newCarId = response.getBody().getId();

        //Get car by id
        response = doFindCarByIdOperation(newCarId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Car carByid = response.getBody();

        Assertions.assertEquals(newCar.getCondition(), carByid.getCondition());
        Assertions.assertEquals(newCar.getDetails().getMileage(), carByid.getDetails().getMileage());
        Assertions.assertNotNull(carByid.getPrice()); //should be populated from pricing service
        Assertions.assertNotNull(carByid.getLocation().getAddress()); //should be populated from maps service
    }

    @Test
    public void canAddACarAndDeleteIt() {
        Car newCar = getCar();
        ResponseEntity<Car> response = doAddCarOperation(newCar);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        //Get the id of the car that was created
        Long newCarId = response.getBody().getId();

        doDeleteCarByIdOperation(newCarId);

        response = doFindCarByIdOperation(newCarId);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void canUpdateCar() {
        Car newCar = getCar();
        ResponseEntity<Car> response = doAddCarOperation(newCar);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        //Get the car that was created
        Car car = response.getBody();
        //Update some details such as mileage, condition, manufacturer
        Integer oldMileage = car.getDetails().getMileage();
        Integer newMileage = oldMileage + 10;
        car.getDetails().setMileage(newMileage);
        car.setCondition(Condition.NEW);
        Manufacturer manufacturer = new Manufacturer(102, "Ford");
        car.getDetails().setManufacturer(manufacturer);

        doUpdateCarOperation(car.getId(), car);

        response = doFindCarByIdOperation(car.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        car = response.getBody();
        Assertions.assertEquals(newMileage, car.getDetails().getMileage());
        Assertions.assertEquals(Condition.NEW, car.getCondition());
        Assertions.assertEquals(manufacturer.getCode(), car.getDetails().getManufacturer().getCode());
        Assertions.assertEquals(manufacturer.getName(), car.getDetails().getManufacturer().getName());

    }

    private void doDeleteCarByIdOperation(Long id) {
        testRestTemplate.delete(URI.create("http://localhost:" + port + "/cars/" + id));
    }

    private ResponseEntity<Car> doAddCarOperation(Car car) {
        HttpEntity<Car> request = new HttpEntity<>(car, new HttpHeaders());
        return testRestTemplate.postForEntity("http://localhost:" + port + "/cars", request, Car.class);
    }

    private ResponseEntity<Car> doFindCarByIdOperation(Long id) {
        return testRestTemplate.getForEntity("http://localhost:" + port + "/cars/" + id, Car.class);
    }

    private void doUpdateCarOperation(Long id, Car car) {
        HttpEntity<Car> request = new HttpEntity<>(car, new HttpHeaders());
        testRestTemplate.put("http://localhost:" + port + "/cars/" + id, request, Car.class);
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }

}
