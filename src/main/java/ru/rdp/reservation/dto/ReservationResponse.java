package ru.rdp.reservation.dto;

import ru.rdp.reservation.model.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO для возврата информации о резерве пользователю.
 */
public record ReservationResponse(
        UUID id,
        UUID productId,
        Integer quantity,
        ReservationStatus status,
        Instant createdAt,
        Instant expiresAt
) {
}
