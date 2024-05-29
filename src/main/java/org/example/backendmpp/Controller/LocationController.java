package org.example.backendmpp.Controller;

import org.example.backendmpp.Model.*;
import org.example.backendmpp.Repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin
public class LocationController {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location convertToLocation(LocationDTO locationDTO){
        Logger logger = LoggerFactory.getLogger(CarController.class);
        logger.info("Converting locationDTO to location");
        logger.info(locationDTO.toString());
        String name = locationDTO.nameOfLocation();
        String address = locationDTO.address();
        return new Location(name, address);
    }

    public LocationResponseDTO convertToLocationDTO(Location location){
        int carCount = location.getCars().size();
        ///long id = location.getId();
        ///get the id and check if it is null
        Long id = location.getId();
        if (id == null) {
            return new LocationResponseDTO(location.getName(), location.getAddress(), 0, carCount);
        }
        return new LocationResponseDTO(location.getName(), location.getAddress(), (int) id.intValue(), carCount);
    }

    @GetMapping("/get")
    public List<LocationResponseDTO> getAllLocations() {
        List<Location> locations = (List<Location>) locationRepository.findAll();
        List<LocationResponseDTO> locationDTOs = locations.stream()
                .map(this::convertToLocationDTO)
                .collect(Collectors.toList());
        return locationDTOs;
    }

    public List<Long> getALlIds(){
        List<Location> locations = (List<Location>) locationRepository.findAll();
        List<Long> ids = locations.stream()
                .map(Location::getId)
                .collect(Collectors.toList());
        return ids;
    }

    @GetMapping("/check-backend-connection")
    public String checkBackendConnection() {
        return "Backend server is reachable.";
    }

    @GetMapping("/get/{id}")
    public List<LocationResponseDTO> getLocationByID(@PathVariable("id") Integer id) {
        Optional<Location> location = locationRepository.findById(Long.valueOf(id));
        return location.map(value -> List.of(convertToLocationDTO(value))).orElse(null);
    }

    @PostMapping("/create")
    public ResponseEntity<LocationResponseDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        try{

            Location newLocation = convertToLocation(locationDTO);

            Location savedLocation = locationRepository.save(newLocation);
            LocationResponseDTO responseDTO = convertToLocationDTO(savedLocation);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }
        catch (Exception e){
            Logger logger = LoggerFactory.getLogger(CarController.class);
            logger.error("Error creating location: " + e.getMessage());
            return ResponseEntity.badRequest().build();        }

    }

    @PutMapping("/update/{id}")
    public LocationResponseDTO updateLocation(@PathVariable("id") Integer id, @RequestBody LocationDTO updatedLocationDTO) {
        Optional<Location> locationOptional = locationRepository.findById(Long.valueOf(id));
        if (locationOptional.isPresent()) {
            Location updatedLocation = convertToLocation(updatedLocationDTO);
            updatedLocation.setId(Long.valueOf(id));
            locationOptional.get().updateLocation(updatedLocation);
            locationRepository.save(updatedLocation);
            return convertToLocationDTO(updatedLocation);
        } else {
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLocation(@PathVariable("id") Integer id) {
        locationRepository.deleteById(Long.valueOf(id));
    }

    @GetMapping("/get/{id}/cars")
    public List<LocationResponseDTO> getLocationCars(@PathVariable("id") Integer id) {
        Optional<Location> location = locationRepository.findById(Long.valueOf(id));
        return location.map(value -> List.of(convertToLocationDTO(value))).orElse(null);
    }
}
