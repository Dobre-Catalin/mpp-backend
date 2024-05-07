package org.example.backendmpp;

import org.example.backendmpp.Controller.LocationController;
import org.example.backendmpp.Model.Location;
import org.example.backendmpp.Model.LocationDTO;
import org.example.backendmpp.Model.LocationResponseDTO;
import org.example.backendmpp.Repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationController locationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllLocations_ReturnsListOfLocations() {
        // Arrange
        Location location1 = new Location("1", "Location 1", new ArrayList<>());
        Location location2 = new Location("2", "Location 2", new ArrayList<>());
        List<Location> locations = Arrays.asList(location1, location2);
        when(locationRepository.findAll()).thenReturn(locations);

        // Act
        List<LocationResponseDTO> response = locationController.getAllLocations();
        // Assert
        assertEquals(2, response.size()); // Assert that the response contains 2 elements

        // You may further validate the content of each LocationResponseDTO object if needed
        assertEquals("Location 1", response.get(0).address());
        assertEquals("Location 2", response.get(1).address());

        verify(locationRepository, times(1)).findAll(); // Verify that the findAll method of the repository was called once
    }

    @Test
    void checkBackendConnection_ReturnsString() {
        // Act
        String response = locationController.checkBackendConnection();
        // Assert
        assertEquals("Backend server is reachable.", response);
    }

    @Test
    void convertToLocation_ReturnsLocation() {
        // Arrange
        LocationController locationController = new LocationController(locationRepository);
        Location location = new Location("1", "Location 1", new ArrayList<>());
        // Act
        LocationResponseDTO response = locationController.convertToLocationDTO(location);
        // Assert
        assertEquals("Location 1", response.address());
        assertEquals("1", response.name());
        assertEquals(0, response.id());
        assertEquals(0, response.carCount());
    }

    @Test
    void convertToLocationDTO_ReturnsLocationResponseDTO() {
        // Arrange
        LocationController locationController = new LocationController(locationRepository);
        LocationDTO locationResponseDTO = new LocationDTO("Location 1", "1");
        // Act
        Location response = locationController.convertToLocation(locationResponseDTO);
        // Assert
        assertEquals("Location 1", response.getName());
    }
}
