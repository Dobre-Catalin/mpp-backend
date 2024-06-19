package org.example.backendmpp.Controller;

import ch.qos.logback.core.joran.sanity.Pair;
import netscape.javascript.JSObject;
import org.apache.tomcat.util.json.JSONFilter;
import org.example.backendmpp.Model.Car;
import org.example.backendmpp.Model.CarDTO;
import org.example.backendmpp.Model.CarResponseDTO;
import org.example.backendmpp.Model.Location;
import org.example.backendmpp.Repository.CarRepository;
import org.example.backendmpp.Repository.LocationRepository;
import org.example.backendmpp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {
    private final CarRepository carRepository;
    private final LocationRepository locationRepository;
    private final AuthenticationService userService;

    @Autowired
    public StatisticsController(CarRepository carRepository, LocationRepository locationRepository, AuthenticationService userRepository) {
        this.carRepository = carRepository;
        this.locationRepository = locationRepository;
        this.userService = userRepository;
    }

    @GetMapping("/get-sales/{brand}")
    public Map<String, String> getSales(@PathVariable String brand) {
        List<Car> cars = carRepository.findAll();
        int sales = 0;
        int maxSale = 0;
        int minSale = 0;
        int averageSale = 0;
        Car mostExpensiveCar = null;
        Car cheapestCar = null;
        for (Car car : cars) {
            if (car.getMake().equals(brand)) {
                sales++;
                averageSale += car.getPrice();
                if (car.getPrice() > maxSale) {
                    maxSale = car.getPrice();
                    mostExpensiveCar = car;
                }
                if (car.getPrice() < minSale) {
                    minSale = car.getPrice();
                    cheapestCar = car;
                }
            }
        }
        averageSale /= sales;
        //create a map with the data
        Map<String, String> statistics = Map.of(
                "sales", String.valueOf(sales),
                "maxSale", String.valueOf(maxSale),
                "minSale", String.valueOf(minSale),
                "averageSale", String.valueOf(averageSale),
                "mostExpensiveCar", mostExpensiveCar.toString(),
                "cheapestCar", cheapestCar.toString()
        );
        return statistics;
    }

    @GetMapping("/sales-make-percentage")
    public Map<String, Float> getSalesMakePercentage() {
        List<Car> cars = carRepository.findCarsBySalePriceNotNull();
        Map<String, Integer> makeCount = Map.of();
        for(Car car : cars) {
            if(makeCount.containsKey(car.getMake())) {
                makeCount.put(car.getMake(), makeCount.get(car.getMake()) + 1);
            } else {
                makeCount.put(car.getMake(), 1);
            }
        }
        int totalCars = cars.size();
        Map<String, Float> makePercentage = Map.of();
        for(Map.Entry<String, Integer> entry : makeCount.entrySet()) {
            makePercentage.put(entry.getKey(), (float) entry.getValue() / totalCars);
        }
        return makePercentage;
    }

    @GetMapping("/sales-location-percentage")
    public Map<String, Float> getSalesLocationPercentage() {
        List<Car> cars = carRepository.findCarsBySalePriceNotNull();
        Map<String, Integer> locationCount = Map.of();
        for(Car car : cars) {
            if(locationCount.containsKey(car.getLocation().getName())) {
                locationCount.put(car.getLocation().getName(), locationCount.get(car.getLocation().getName()) + 1);
            } else {
                locationCount.put(car.getLocation().getName(), 1);
            }
        }
        int totalCars = cars.size();
        Map<String, Float> locationPercentage = Map.of();
        for(Map.Entry<String, Integer> entry : locationCount.entrySet()) {
            locationPercentage.put(entry.getKey(), (float) entry.getValue() / totalCars);
        }
        return locationPercentage;
    }

    @GetMapping("most-expensive-sale-ever")
    public Car getMostExpensiveSaleEver() {
        List<Car> cars = carRepository.findCarsBySalePriceNotNull();
        Car mostExpensiveCar = null;
        int maxSale = 0;
        for(Car car : cars) {
            if(car.getPrice() > maxSale) {
                maxSale = car.getPrice();
                mostExpensiveCar = car;
            }
        }
        return mostExpensiveCar;
    }

    @PostMapping("/recommend-car")
    public List<CarResponseDTO> recommendCar(@RequestBody Map<String, Map<String, String>> data) {
        List<Integer> locations = userService.getUserLocations();
        List<Location> locationsFull = new java.util.ArrayList<>(List.of());
        for(Integer locationId : locations) {
            Location location = locationRepository.findById(Long.valueOf(locationId)).orElse(null);
            if(location != null) {
                locationsFull.add(location);
            }
        }
        List<Car> cars = carRepository.findCarsBySalePriceIsNullAndLocationIsIn(locationsFull);

        List<CarResponseDTO> recommendedCars = new java.util.ArrayList<>(List.of());
        List<Map<Car, Float>> carScores = new java.util.ArrayList<>(List.of());
        for(Car car : cars){
            float score = 0;
            for(Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                String attribute = entry.getKey();
                Map<String, String> value = entry.getValue();
                /// if make is the same, award the inner map's value, otherwise award 0
                if(attribute.equals("make")) {
                    if(car.getMake().equals(value.get("first"))) {
                        score += Float.parseFloat(value.get("second"));
                    }
                }
                /// if model is the same, award the inner map's value, otherwise award 0
                if(attribute.equals("model")) {
                    if(car.getModel().equals(value.get("first"))) {
                        score += Float.parseFloat(value.get("second"));
                    }
                }
                /// if year is the same, award the inner map's value, otherwise award 0
                if(attribute.equals("year")) {
                    if(car.getYear() >= Integer.parseInt(value.get("first"))) {
                        score += Float.parseFloat(value.get("second"));
                    }
                }
                /// if mileage is the same, award the inner map's value, otherwise award 0
                if(attribute.equals("mileage")) {
                    if(car.getMileage() <= Integer.parseInt(value.get("first"))) {
                        score += Float.parseFloat(value.get("second"));
                    }
                }
                /// if price is the same, award the inner map's value, otherwise award 0
                if(attribute.equals("price")) {
                    if(car.getPrice() <= Integer.parseInt(value.get("first"))) {
                        score += Float.parseFloat(value.get("second"));
                    }
                }
                /// if accidents is the same, award the inner map's value, otherwise award 0
                if(attribute.equals("accidents")) {
                    if(car.getAccidents() <= Integer.parseInt(value.get("first"))) {
                        score += Float.parseFloat(value.get("second"));
                    }
                }
            }
            if(carScores.size() < 3) {
                ///add the car to the list along with the score
                carScores.add(Map.of(car, score));
            }
            else {
                //check if the car has a higher score than the lowest score
                float lowestScore = carScores.get(0).values().iterator().next();
                if(score > lowestScore) {
                    carScores.remove(0);
                    carScores.add(Map.of(car, score));
                }
            }
        }
        for(Map<Car, Float> carScore : carScores) {
            Car car = carScore.keySet().iterator().next();
            recommendedCars.add(new CarResponseDTO(car.getId(), car.getMake(),
                    car.getModel(), car.getYear(), car.getColor(), car.getMileage(),
                    car.getAccidents(), car.getEngineCapacity(), car.getEngineType(), car.getPrice(),
                    car.getLocation().getName(), car.getAbout(), car.getSalePrice(), car.getSaleDate()));
        }
        return recommendedCars;
    }

    @PostMapping("/decide-price-purchase")
    public int decidePricePurchase(@RequestBody CarDTO carDTO) {
        List<Car> cars = carRepository.findCarsBySalePriceIsNotNull();
        List<Float> priceComparisons = new ArrayList<>();

        for (Car car : cars) {
            if (car.getMake().equals(carDTO.make()) && car.getModel().equals(carDTO.model())) {
                float adjustedPrice = carDTO.price();

                if (Math.abs(car.getYear() - carDTO.year()) > 3) {
                    if (car.getYear() > carDTO.year()) {
                        adjustedPrice -= (float) (car.getPrice() * 0.1);
                    } else {
                        adjustedPrice += (float) (car.getPrice() * 0.2);
                    }
                }

                if (Math.abs(car.getMileage() - carDTO.mileage()) > 20000) {
                    adjustedPrice -= (float) (car.getPrice() * 0.1);
                } else {
                    adjustedPrice += (float) (car.getPrice() * 0.2);
                }

                if (car.getAccidents() < carDTO.accidents()) {
                    adjustedPrice -= (float) (carDTO.price() * 0.1 * carDTO.accidents());
                } else {
                    adjustedPrice += (float) (carDTO.price() * 0.1 * carDTO.accidents());
                }

                if (car.getEngineCapacity() > carDTO.engineCapacity()) {
                    adjustedPrice += (float) (car.getPrice() * 0.1);
                }

                priceComparisons.add(adjustedPrice);
            }
        }

        if (priceComparisons.isEmpty()) {
            return carDTO.price();
        }

        float average = 0;
        for (float priceComparison : priceComparisons) {
            average += priceComparison;
        }
        average /= priceComparisons.size();

        if (average >= carDTO.price() * 0.85 && average <= carDTO.price() * 1.15) {
            return carDTO.price();
        }

        return (int) average;
    }
}
