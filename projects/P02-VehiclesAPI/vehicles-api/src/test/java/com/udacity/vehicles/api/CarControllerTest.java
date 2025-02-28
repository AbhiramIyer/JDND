package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Implements testing of the CarController class.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @BeforeEach
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        Car car = getCar();
        car.setId(1L);
        mvc.perform(get("/cars").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[0].id").value(car.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[0].details.model").value(car.getDetails().getModel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[0].details.mileage").value(car.getDetails().getMileage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.carList[0].condition").value(car.getCondition().toString()))
        ;
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        mvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(car.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.model").value(car.getDetails().getModel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.mileage").value(car.getDetails().getMileage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.condition").value(car.getCondition().toString()))
        ;
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        mvc.perform(delete("/cars/1"))
                .andExpect(status().isOk());
    }

    /**
     * Tests the updation of a car's id using the PUT method
     * @throws Exception if the PUT operation on a vehicle fails
     */
    @Test
    public void updateCarId() throws Exception {
        Car car = getCar();
        car.setId(2L);
        // update the car id to 1
        mvc.perform(put("/cars/1")
                .content(json.write(car).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
        ;
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
