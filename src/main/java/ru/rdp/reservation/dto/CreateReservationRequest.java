package ru.rdp.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO для входящего запроса на создание резерва.
 */
public record CreateReservationRequest(
        @NotNull(message = "Product ID cannot be null")
        UUID productId,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Reservation quantity must be at least 1")
        Integer quantity
) {
}
