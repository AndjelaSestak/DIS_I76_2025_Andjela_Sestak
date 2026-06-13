package com.hotelreservation.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelreservation.hotel.dto.HotelRequest;
import com.hotelreservation.hotel.dto.HotelResponse;
import com.hotelreservation.hotel.exception.HotelNotFoundException;
import com.hotelreservation.hotel.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    private HotelResponse hotelResponse;
    private HotelRequest hotelRequest;

    @BeforeEach
    void setUp() {
        hotelResponse = HotelResponse.builder()
                .id(1L)
                .name("Grand Hotel Belgrade")
                .city("Belgrade")
                .address("Knez Mihailova 10")
                .description("Luxury hotel")
                .stars(5)
                .amenities("WiFi, Pool, Spa")
                .active(true)
                .build();

        hotelRequest = new HotelRequest();
        hotelRequest.setName("Grand Hotel Belgrade");
        hotelRequest.setCity("Belgrade");
        hotelRequest.setAddress("Knez Mihailova 10");
        hotelRequest.setDescription("Luxury hotel");
        hotelRequest.setStars(5);
        hotelRequest.setAmenities("WiFi, Pool, Spa");
    }

    @Test
    void createHotel_Returns201() throws Exception {
        when(hotelService.createHotel(any(HotelRequest.class))).thenReturn(hotelResponse);

        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Grand Hotel Belgrade"))
                .andExpect(jsonPath("$.city").value("Belgrade"));
    }

    @Test
    void getHotelById_Returns200() throws Exception {
        when(hotelService.getHotelById(1L)).thenReturn(hotelResponse);

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Grand Hotel Belgrade"));
    }

    @Test
    void getHotelById_NotFound_Returns404() throws Exception {
        when(hotelService.getHotelById(99L)).thenThrow(new HotelNotFoundException(99L));

        mockMvc.perform(get("/api/hotels/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllHotels_Returns200() throws Exception {
        when(hotelService.getAllHotels()).thenReturn(List.of(hotelResponse));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Grand Hotel Belgrade"));
    }

    @Test
    void getHotelsByCity_Returns200() throws Exception {
        when(hotelService.getHotelsByCity("Belgrade")).thenReturn(List.of(hotelResponse));

        mockMvc.perform(get("/api/hotels/city/Belgrade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Belgrade"));
    }

    @Test
    void updateHotel_Returns200() throws Exception {
        when(hotelService.updateHotel(eq(1L), any(HotelRequest.class))).thenReturn(hotelResponse);

        mockMvc.perform(put("/api/hotels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grand Hotel Belgrade"));
    }
}
