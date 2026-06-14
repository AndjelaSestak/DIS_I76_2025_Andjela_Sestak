package com.hotelreservation.reservation.service;

import com.hotelreservation.reservation.client.HotelClient;
import com.hotelreservation.reservation.config.RabbitMQConfig;
import com.hotelreservation.reservation.dto.ReservationRequest;
import com.hotelreservation.reservation.dto.ReservationResponse;
import com.hotelreservation.reservation.dto.RoomResponse;
import com.hotelreservation.reservation.event.ReservationEvent;
import com.hotelreservation.reservation.exception.ReservationNotFoundException;
import com.hotelreservation.reservation.exception.RoomNotAvailableException;
import com.hotelreservation.reservation.model.Reservation;
import com.hotelreservation.reservation.model.ReservationStatus;
import com.hotelreservation.reservation.repository.ReservationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final HotelClient hotelClient;
    private final RabbitTemplate rabbitTemplate;

    public ReservationService(ReservationRepository reservationRepository,
                               HotelClient hotelClient,
                               RabbitTemplate rabbitTemplate) {
        this.reservationRepository = reservationRepository;
        this.hotelClient = hotelClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @CircuitBreaker(name = "hotel-service", fallbackMethod = "createReservationFallback")
    public ReservationResponse createReservation(ReservationRequest request) {
        RoomResponse room = hotelClient.getRoomById(request.getRoomId());

        if (!Boolean.TRUE.equals(room.getAvailable())) {
            throw new RoomNotAvailableException(request.getRoomId());
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Reservation reservation = Reservation.builder()
                .reservationNumber(generateReservationNumber())
                .userId(request.getUserId())
                .roomId(request.getRoomId())
                .hotelId(room.getHotelId())
                .guestName(request.getGuestName())
                .guestEmail(request.getGuestEmail())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(ReservationStatus.CONFIRMED)
                .build();

        reservation = reservationRepository.save(reservation);

        hotelClient.updateRoomAvailability(request.getRoomId(), false);

        publishReservationEvent(reservation);

        log.info("Reservation {} created successfully", reservation.getReservationNumber());
        return ReservationResponse.fromReservation(reservation);
    }

    public ReservationResponse createReservationFallback(ReservationRequest request, Exception ex) {
        log.error("Hotel service unavailable, cannot create reservation: {}", ex.getMessage());
        throw new IllegalStateException("Hotel service is currently unavailable. Please try again later.");
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        return ReservationResponse.fromReservation(
                reservationRepository.findById(id)
                        .orElseThrow(() -> new ReservationNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationByNumber(String reservationNumber) {
        return ReservationResponse.fromReservation(
                reservationRepository.findByReservationNumber(reservationNumber)
                        .orElseThrow(() -> new ReservationNotFoundException(reservationNumber)));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::fromReservation)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(ReservationResponse::fromReservation)
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "hotel-service", fallbackMethod = "cancelReservationFallback")
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("Reservation is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);

        hotelClient.updateRoomAvailability(reservation.getRoomId(), true);

        log.info("Reservation {} cancelled", reservation.getReservationNumber());
        return ReservationResponse.fromReservation(reservation);
    }

    public ReservationResponse cancelReservationFallback(Long id, Exception ex) {
        log.error("Hotel service unavailable during cancellation: {}", ex.getMessage());
        throw new IllegalStateException("Hotel service is currently unavailable. Please try again later.");
    }

    private void publishReservationEvent(Reservation reservation) {
        ReservationEvent event = ReservationEvent.builder()
                .reservationNumber(reservation.getReservationNumber())
                .userId(reservation.getUserId())
                .roomId(reservation.getRoomId())
                .hotelId(reservation.getHotelId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RESERVATION_EXCHANGE,
                RabbitMQConfig.RESERVATION_CREATED_ROUTING_KEY,
                event);

        log.info("Published reservation event for {}", reservation.getReservationNumber());
    }

    private String generateReservationNumber() {
        String number;
        do {
            number = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (reservationRepository.existsByReservationNumber(number));
        return number;
    }
}
