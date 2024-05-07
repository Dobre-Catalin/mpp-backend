package org.example.backendmpp.Model;

public record LocationResponseDTO(
        String name,
        String address,
        int id,
        int carCount
) {
}
