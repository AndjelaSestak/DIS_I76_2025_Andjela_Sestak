package com.hotelreservation.payment.repository;

import com.hotelreservation.payment.model.Payment;
import com.hotelreservation.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Optional<Payment> findByReservationNumber(String reservationNumber);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByPaymentNumber(String paymentNumber);
}
