package com.hotelreservation.payment.service;

import com.hotelreservation.payment.dto.PaymentResponse;
import com.hotelreservation.payment.event.ReservationEvent;
import com.hotelreservation.payment.exception.PaymentNotFoundException;
import com.hotelreservation.payment.model.Payment;
import com.hotelreservation.payment.model.PaymentStatus;
import com.hotelreservation.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private ReservationEvent reservationEvent;
    private Payment payment;

    @BeforeEach
    void setUp() {
        reservationEvent = ReservationEvent.builder()
                .reservationNumber("RES-001")
                .userId(1L)
                .roomId(1L)
                .hotelId(1L)
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .totalPrice(new BigDecimal("200.00"))
                .status("CONFIRMED")
                .build();

        payment = Payment.builder()
                .id(1L)
                .paymentNumber("PAY-ABCD1234")
                .reservationNumber("RES-001")
                .userId(1L)
                .amount(new BigDecimal("200.00"))
                .status(PaymentStatus.COMPLETED)
                .guestEmail("john@example.com")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void processPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponse response = paymentService.processPayment(reservationEvent);

        assertThat(response).isNotNull();
        assertThat(response.getReservationNumber()).isEqualTo("RES-001");
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
        verify(paymentRepository).save(any(Payment.class));
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void getAllPayments_ReturnsAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<PaymentResponse> responses = paymentService.getAllPayments();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getPaymentNumber()).isEqualTo("PAY-ABCD1234");
    }

    @Test
    void getPaymentById_Found() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(99L))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getPaymentByNumber_Found() {
        when(paymentRepository.findByPaymentNumber("PAY-ABCD1234")).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentByNumber("PAY-ABCD1234");

        assertThat(response.getPaymentNumber()).isEqualTo("PAY-ABCD1234");
    }

    @Test
    void getPaymentByNumber_NotFound_ThrowsException() {
        when(paymentRepository.findByPaymentNumber("PAY-UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByNumber("PAY-UNKNOWN"))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getPaymentByReservationNumber_Found() {
        when(paymentRepository.findByReservationNumber("RES-001")).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentByReservationNumber("RES-001");

        assertThat(response.getReservationNumber()).isEqualTo("RES-001");
    }

    @Test
    void getPaymentsByUser_ReturnsUserPayments() {
        when(paymentRepository.findByUserId(1L)).thenReturn(List.of(payment));

        List<PaymentResponse> responses = paymentService.getPaymentsByUser(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void getPaymentsByStatus_ReturnsMatchingPayments() {
        when(paymentRepository.findByStatus(PaymentStatus.COMPLETED)).thenReturn(List.of(payment));

        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}
