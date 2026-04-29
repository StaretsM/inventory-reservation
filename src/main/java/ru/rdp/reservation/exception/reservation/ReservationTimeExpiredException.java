package ru.rdp.reservation.exception.reservation;

public class ReservationTimeExpiredException extends AbstractReservationException{
    public ReservationTimeExpiredException(String message) {
        super(message);
    }
}
