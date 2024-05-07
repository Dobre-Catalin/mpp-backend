package org.example.backendmpp.Model;

public record CarResponseDTO(
        Integer ID,
        String make,
        String model,
        int year,
        String color,
        int mileage,
        int accidents,
        int engineCapacity,
        String engineType,
        int price,
        String location,
        String about
) {
}
