package org.example.backendmpp;

import org.example.backendmpp.Controller.CarController;
import org.example.backendmpp.Model.Car;
import org.example.backendmpp.Model.CarDTO;
import org.example.backendmpp.Model.CarResponseDTO;
import org.example.backendmpp.Model.Location;
import org.example.backendmpp.Repository.CarRepository;
import org.example.backendmpp.Repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CarControllerTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarController carController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllCars() {
        // Arrange
        List<Car> cars = Arrays.asList(
        );
        when(carRepository.findAll()).thenReturn(cars);

        // Act
        List<CarResponseDTO> result = carController.getAllCars();

        // Assert
        assertEquals(cars.size(), result.size());
    }

    @Test
    void testGetCarById() {
        // Arrange
        int id = 1;
        Car car = new Car("Toyota", "Camry", 2020, "Black", 20000, 0, 2000, "Petrol", 25000, "Good condition", new Location());

        when(carRepository.findById(id)).thenReturn(Optional.of(car));

        // Act
        ResponseEntity<CarResponseDTO> result = carController.getCarById(id);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(car.getMake(), result.getBody().make());

    }

    @Test
    void testAddCar() {
        // Arrange
        Location loc = new Location();
        CarDTO carDTO = new CarDTO("Toyota", "Camry", 2020, "Black", 20000, 0, 2000, "Petrol", 25000, "Good condition", 1);
        Car car = new Car("Toyota", "Camry", 2020, "Black", 20000, 0, 2000, "Petrol", 25000, "Good condition", loc);

        when(carRepository.save(any(Car.class))).thenReturn(car);

        // Act
        ResponseEntity<CarResponseDTO> result = carController.createCar(carDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(car.getMake(), result.getBody().make());
    }

    @Test
    void testUpdateCar() {
        // Arrange
        int id = 1;
        Location loc = new Location();
        CarDTO carDTO = new CarDTO("Toyota", "Camry", 2020, "Black", 20000, 0, 2000, "Petrol", 25000, "Good condition", 1);
        Car car = new Car("Toyota", "Camry", 2020, "Black", 20000, 0, 2000, "Petrol", 25000, "Good condition", loc);

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        // Act
        ResponseEntity<CarResponseDTO> result = carController.updateCar(id, carDTO);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(car.getMake(), result.getBody().make());
    }
}
