package ru.rdp.reservation.service.reservation;

import ru.rdp.reservation.dto.CreateReservationRequest;
import ru.rdp.reservation.dto.ReservationResponse;

import java.util.UUID;

public interface ReservationService {

    /**
     * Создание нового резерва.
     * @param request DTO с ID продукта и количеством
     * @return DTO с данными созданного резерва
     */
    ReservationResponse createReservation(CreateReservationRequest request);

    /**
     * Подтверждение существующего резерва.
     *
     * @param reservationId ID резерва, который нужно подтвердить
     * @return DTO с обновленными данными резерва
     */
    ReservationResponse confirmReservation(UUID reservationId);

}
