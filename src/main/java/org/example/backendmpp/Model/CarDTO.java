package org.example.backendmpp.Model;

import java.util.Date;

public record CarDTO(
        String make,
        String model,
        int year,
        String color,
        int mileage,
        int accidents,
        int engineCapacity,
        String engineType,
        int price,
        String about,
        int locationId,
        Integer salePrice,
        Date saleDate
) {
}
