package ru.rdp.reservation.exception.reservation;

public class InvalidReservationStatusException extends AbstractReservationException {
    public InvalidReservationStatusException(String message) {
        super(message);
    }
}
