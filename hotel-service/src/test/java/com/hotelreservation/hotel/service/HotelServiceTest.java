package com.hotelreservation.hotel.service;

import com.hotelreservation.hotel.dto.HotelRequest;
import com.hotelreservation.hotel.dto.HotelResponse;
import com.hotelreservation.hotel.exception.HotelNotFoundException;
import com.hotelreservation.hotel.model.Hotel;
import com.hotelreservation.hotel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;
    private HotelRequest hotelRequest;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
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
    void createHotel_Success() {
        when(hotelRepository.existsByNameAndCity(any(), any())).thenReturn(false);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        HotelResponse response = hotelService.createHotel(hotelRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Grand Hotel Belgrade");
        assertThat(response.getCity()).isEqualTo("Belgrade");
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void createHotel_AlreadyExists_ThrowsException() {
        when(hotelRepository.existsByNameAndCity(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> hotelService.createHotel(hotelRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getHotelById_Found() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        HotelResponse response = hotelService.getHotelById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Grand Hotel Belgrade");
    }

    @Test
    void getHotelById_NotFound_ThrowsException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(99L))
                .isInstanceOf(HotelNotFoundException.class);
    }

    @Test
    void getAllHotels_ReturnsList() {
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<HotelResponse> responses = hotelService.getAllHotels();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Grand Hotel Belgrade");
    }

    @Test
    void getActiveHotels_ReturnsList() {
        when(hotelRepository.findByActiveTrue()).thenReturn(List.of(hotel));

        List<HotelResponse> responses = hotelService.getActiveHotels();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getActive()).isTrue();
    }

    @Test
    void getHotelsByCity_ReturnsList() {
        when(hotelRepository.findByCityIgnoreCase("Belgrade")).thenReturn(List.of(hotel));

        List<HotelResponse> responses = hotelService.getHotelsByCity("Belgrade");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCity()).isEqualTo("Belgrade");
    }

    @Test
    void updateHotel_Success() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        HotelRequest updateRequest = new HotelRequest();
        updateRequest.setDescription("Updated description");

        HotelResponse response = hotelService.updateHotel(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    void deactivateHotel_Success() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        hotelService.deactivateHotel(1L);

        assertThat(hotel.getActive()).isFalse();
        verify(hotelRepository).save(hotel);
    }

    @Test
    void deleteHotel_Success() {
        when(hotelRepository.existsById(1L)).thenReturn(true);
        doNothing().when(hotelRepository).deleteById(1L);

        hotelService.deleteHotel(1L);

        verify(hotelRepository).deleteById(1L);
    }

    @Test
    void deleteHotel_NotFound_ThrowsException() {
        when(hotelRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> hotelService.deleteHotel(99L))
                .isInstanceOf(HotelNotFoundException.class);
    }
}
