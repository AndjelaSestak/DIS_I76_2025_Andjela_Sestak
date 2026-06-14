package com.hotelreservation.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotelreservation.payment.dto.PaymentResponse;
import com.hotelreservation.payment.exception.GlobalExceptionHandler;
import com.hotelreservation.payment.exception.PaymentNotFoundException;
import com.hotelreservation.payment.model.PaymentStatus;
import com.hotelreservation.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private ObjectMapper objectMapper;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        paymentResponse = PaymentResponse.builder()
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
    void getAllPayments_Returns200() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of(paymentResponse));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentNumber").value("PAY-ABCD1234"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void getPaymentById_Found_Returns200() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservationNumber").value("RES-001"));
    }

    @Test
    void getPaymentById_NotFound_Returns404() throws Exception {
        when(paymentService.getPaymentById(99L)).thenThrow(new PaymentNotFoundException(99L));

        mockMvc.perform(get("/api/payments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPaymentByNumber_Found_Returns200() throws Exception {
        when(paymentService.getPaymentByNumber("PAY-ABCD1234")).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments/number/PAY-ABCD1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNumber").value("PAY-ABCD1234"));
    }

    @Test
    void getPaymentsByUser_Returns200() throws Exception {
        when(paymentService.getPaymentsByUser(1L)).thenReturn(List.of(paymentResponse));

        mockMvc.perform(get("/api/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getPaymentsByStatus_Returns200() throws Exception {
        when(paymentService.getPaymentsByStatus(PaymentStatus.COMPLETED)).thenReturn(List.of(paymentResponse));

        mockMvc.perform(get("/api/payments/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }
}
