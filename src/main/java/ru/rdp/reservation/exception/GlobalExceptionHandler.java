package ru.rdp.reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.rdp.reservation.dto.ErrorResponse;
import ru.rdp.reservation.exception.product.InsufficientStockException;
import ru.rdp.reservation.exception.product.ProductNotFoundException;
import ru.rdp.reservation.exception.reservation.InvalidReservationStatusException;
import ru.rdp.reservation.exception.reservation.ReservationNotFoundException;
import ru.rdp.reservation.exception.reservation.ReservationTimeExpiredException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ProductNotFoundException.class,
            ReservationNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(RuntimeException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
    }

    @ExceptionHandler({
            InsufficientStockException.class,
            InvalidReservationStatusException.class,
            ReservationTimeExpiredException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(RuntimeException ex) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
    }
}
