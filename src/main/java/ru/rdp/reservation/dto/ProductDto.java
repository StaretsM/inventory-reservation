package ru.rdp.reservation.dto;

import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        Integer stock
) {
}
