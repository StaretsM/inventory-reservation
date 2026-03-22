package ru.rdp.reservation.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rdp.reservation.dto.CreateReservationRequest;
import ru.rdp.reservation.dto.ReservationResponse;

import java.util.UUID;

@RequestMapping("/reservations")
public interface ReservationController {

    @PostMapping
    ReservationResponse createReservation(
            @RequestBody CreateReservationRequest request
    );

    @PostMapping("/{id}/confirm")
    ReservationResponse confirmReservation(
            @PathVariable UUID id
    );
}
