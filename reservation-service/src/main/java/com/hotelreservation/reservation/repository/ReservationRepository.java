package com.hotelreservation.reservation.repository;

import com.hotelreservation.reservation.model.Reservation;
import com.hotelreservation.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationNumber(String reservationNumber);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByStatus(ReservationStatus status);

    boolean existsByReservationNumber(String reservationNumber);
}
