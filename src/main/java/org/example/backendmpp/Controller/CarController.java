package org.example.backendmpp.Controller;

import jakarta.transaction.Transactional;
import org.example.backendmpp.Model.*;
import org.example.backendmpp.Model.User.User;
import org.example.backendmpp.Repository.CarRepository;
import org.example.backendmpp.Repository.CarRepository;
import org.example.backendmpp.Repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin
public class CarController {

    private final CarRepository carRepository;

    Integer entitiesSentUpTo;
    private List<Integer> locationIdsAccessible = List.of();
    private Integer lastPageSent = -1;
    Page lastPageSentContent = Page.empty();

    Integer entitiesToAdd;
    Integer interval;
    Boolean intervalFlag;
    List<String> carMakes = List.of("Toyota", "Honda", "Ford", "Chevrolet", "Nissan", "Volkswagen", "Mercedes-Benz", "BMW", "Audi", "Hyundai");
    List<String> carModels=List.of("Camry", "Civic", "F-150", "Silverado", "Altima", "Jetta", "C-Class", "3 Series", "A4", "Elantra");
    List<String> carColors=List.of("Black", "White", "Red", "Blue", "Silver", "Gray", "Green", "Brown", "Yellow", "Orange");
    List<String> carEngineTypes=List.of("Petrol", "Diesel", "Electric", "Hybrid");
    Logger logger = LoggerFactory.getLogger(CarController.class);
    private final WebSocketHandler webSocketHandler;
    @Autowired
    private LocationController locationController;
    @Autowired
    private AuthenticationController authenticationController;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("/location-ids")
    public List<Long> getLocationIdsFromLocationService() {
        return locationController.getALlIds();
    }

    @Autowired
    public CarController(CarRepository carRepository) {
        entitiesToAdd = 0;
        interval = 9999999;
        intervalFlag = true;
        this.carRepository = carRepository;

        entitiesSentUpTo = 0;

        webSocketHandler = new WebSocketHandler();

    }

    @GetMapping("/check-backend-connection")
    public ResponseEntity<String> checkBackendConnection() {
        return ResponseEntity.ok("Backend server is reachable.");
    }

    @RequestMapping(value = "/get-more-entities", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseEntity<Page<CarResponseDTO>> getMoreEntities(@PageableDefault(size = 50) Pageable pageable) {
        Collection<Location> locations = new ArrayList<>();

        //check if locationIdAccessible is empty
        if(locationIdsAccessible.isEmpty()){
            locationIdsAccessible = authenticationService.getUserLocations();

            if(locationIdsAccessible.isEmpty()){
                return ResponseEntity.badRequest().build();
            }

            for (Integer locationId : locationIdsAccessible) {
                Optional<Location> location = locationRepository.findById(locationId.longValue());
                location.ifPresent(locations::add);
            }
            lastPageSent = -1;
        }
        System.out.println("Location ids accessible: " + locationIdsAccessible);
        ///check if locationIdsAccessible is the same as locationIds
        if(!locationIdsAccessible.equals(authenticationService.getUserLocations())){
            locationIdsAccessible = authenticationService.getUserLocations();

            for (Integer locationId : locationIdsAccessible) {
                Optional<Location> location = locationRepository.findById(locationId.longValue());
                location.ifPresent(locations::add);
            }
            lastPageSent = -1;
        }

        if(lastPageSent == pageable.getPageNumber()){
            return ResponseEntity.ok(lastPageSentContent);
        }

        Page<Car> carsPage = carRepository.findCarsByLocationIsInAndSalePriceIsNull(locations, pageable);
        Page<CarResponseDTO> carDTOsPage = carsPage.map(this::convertToCarResponseDTO);

        lastPageSent++;
        lastPageSentContent = carDTOsPage;
        return ResponseEntity.ok(carDTOsPage);
    }

    @GetMapping("/get-sellable")
    @CrossOrigin
    public List<CarResponseDTO> getSellableCars() {
        List<Car> cars = carRepository.findCarsBySalePriceIsNull();
        List<Integer> locationIds = authenticationService.getUserLocations();
        List<Car> sellableCars = new ArrayList<>();
        for (Car car : cars) {
            if(locationIds.contains(car.getLocation().getId().intValue())){
                sellableCars.add(car);
            }
        }
        List<CarResponseDTO> carDTOs = sellableCars.stream()
                .map(this::convertToCarResponseDTO)
                .collect(Collectors.toList());
        return carDTOs;
    }

    @GetMapping("/get/sold/{id}")
    @CrossOrigin
    public ResponseEntity<List<CarResponseDTO>> getSoldCarsById(@PathVariable("id") Integer id) {
        Location location = locationRepository.findById(id.longValue()).orElseThrow();
        List<Car> cars = carRepository.findCarsBySalePriceIsNotNullAndLocation(location);
        if(cars.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        //send all the cars as response dtos
        List<CarResponseDTO> carDTOs = cars.stream()
                .map(this::convertToCarResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(carDTOs);
    }

    @PostMapping("sell/{id}")
    @CrossOrigin
    public String sellCar(@PathVariable("id") Integer id, @RequestBody SaleDTO saleDTO) {
        Car car = carRepository.findById(id).orElseThrow();
        if(saleDTO.price() < 0){
            return "Invalid sale price";
        }
        //if the car is sold at more than 20% of the original price, don't allow the sale
        if(car.getPrice() > saleDTO.price() * 1.2){
            return "Invalid sale price";
        }
        car.setSalePrice(saleDTO.price());
        car.setSaleDate(saleDTO.date());
        carRepository.save(car);
        return "Car sold";
    }

/*
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/get")
    public List<CarResponseDTO> getAllCars() {
        List<Car> cars = (List<Car>) carRepository.findAll();
        List<CarResponseDTO> carDTOs = cars.stream()
                .map(this::convertToCarResponseDTO)
                .collect(Collectors.toList());
        return carDTOs;
    }*/

    @GetMapping("/get/{id}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable("id") Integer id) {
        Optional<Car> carOptional = carRepository.findById(id);
        return carOptional.map(car -> ResponseEntity.ok(convertToCarResponseDTO(car)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<CarResponseDTO> createCar(@RequestBody CarDTO carDTO) {
        try{
            Car newCar = convertToCar(carDTO);
            Car savedCar = carRepository.save(newCar);
            CarResponseDTO responseDTO = convertToCarResponseDTO(savedCar);
            System.out.println("Car created");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CarResponseDTO> updateCar(@PathVariable("id") Integer id, @RequestBody CarDTO updatedCarDTO) {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isPresent()) {
            Car updatedCar = convertToCar(updatedCarDTO);
            updatedCar.setId(id);
            carOptional.get().updateCar(updatedCar);
            carRepository.save(updatedCar);
            CarResponseDTO responseDTO = convertToCarResponseDTO(updatedCar);
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable("id") Integer id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Car convertToCar(CarDTO carDTO) {
        Location location = new Location();
        location.setId((long) carDTO.locationId());
        return new Car(carDTO.make(), carDTO.model(), carDTO.year(),
                carDTO.color(), carDTO.mileage(), carDTO.accidents(), carDTO.engineCapacity(),
                carDTO.engineType(), carDTO.price(), carDTO.about(), location, carDTO.salePrice(), carDTO.saleDate());
    }

    private CarResponseDTO convertToCarResponseDTO(Car car) {
        return new CarResponseDTO(car.getId(), car.getMake(), car.getModel(), car.getYear(),
                car.getColor(), car.getMileage(), car.getAccidents(), car.getEngineCapacity(),
                car.getEngineType(), car.getPrice(), car.getAbout(), car.getLocation().getName(), car.getSalePrice(), car.getSaleDate());
    }

    @PostMapping("/settings")
    public ResponseEntity<Void> addEntities(@RequestBody SettingsDTO settingsDTO) {
        this.entitiesToAdd = settingsDTO.entitiesToAdd();
        this.interval = settingsDTO.interval();
        this.intervalFlag = true;
        return ResponseEntity.noContent().build();
    }

    Integer getInterval() {
        return this.interval;
    }

    @Scheduled(fixedDelay = 500000)
    public void addEntities() {
        if (this.entitiesToAdd > 0) {
            for (int i = 0; i < this.entitiesToAdd; i++) {

                Integer randomNumber = 1000 + (int) (Math.random() * 99999);
                Integer randomMake = (int) (Math.random() * carMakes.size());
                Integer randomModel = (int) (Math.random() * carModels.size());
                Integer randomColor = (int) (Math.random() * carColors.size());
                Integer randomEngineType = (int) (Math.random() * carEngineTypes.size());

                Long logcationId = locationController.getALlIds().get((int) (Math.random() * locationController.getALlIds().size()));
                Location location = new Location();
                location.setId(logcationId);

                CarDTO carDTO = new CarDTO(carMakes.get(randomMake), carModels.get(randomModel), 2020, carColors.get(randomColor), 20000, 0, 2000, carEngineTypes.get(randomEngineType), randomNumber, "Good condition", logcationId.intValue(), null, null);
                Car car = convertToCar(carDTO);

                carRepository.save(car);

            }
        }
        this.sendUpdateSignal("New cars added");
    }

    public void sendUpdateSignal(String updateMessage) {
        try {
            webSocketHandler.sendUpdateToAllClients(updateMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


