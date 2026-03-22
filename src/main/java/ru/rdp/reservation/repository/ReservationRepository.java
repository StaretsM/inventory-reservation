package ru.rdp.reservation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rdp.reservation.model.Product;
import ru.rdp.reservation.model.Reservation;
import ru.rdp.reservation.model.ReservationStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByProductIdAndStatusAndExpiresAtBefore(
            UUID productId,
            ReservationStatus status,
            Instant time
    );

    @Query("""
            SELECT r.product FROM Reservation r
            WHERE r.status = :status AND r.createdAt >= :sinceTime
            GROUP BY r.product
            ORDER BY SUM(r.quantity) DESC
            """)
    List<Product> findTopReserved(@Param("sinceTime") Instant sinceTime,
                                  Pageable limit, @Param("status") ReservationStatus status);
}
