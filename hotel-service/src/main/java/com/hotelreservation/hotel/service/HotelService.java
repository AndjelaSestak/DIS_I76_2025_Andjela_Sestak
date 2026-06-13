package com.hotelreservation.hotel.service;

import com.hotelreservation.hotel.dto.HotelRequest;
import com.hotelreservation.hotel.dto.HotelResponse;
import com.hotelreservation.hotel.exception.HotelNotFoundException;
import com.hotelreservation.hotel.model.Hotel;
import com.hotelreservation.hotel.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public HotelResponse createHotel(HotelRequest request) {
        if (hotelRepository.existsByNameAndCity(request.getName(), request.getCity())) {
            throw new IllegalArgumentException("Hotel with name '" + request.getName() + "' already exists in " + request.getCity());
        }
        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .description(request.getDescription())
                .stars(request.getStars())
                .amenities(request.getAmenities())
                .active(true)
                .build();
        return HotelResponse.fromHotel(hotelRepository.save(hotel));
    }

    @Transactional(readOnly = true)
    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        return HotelResponse.fromHotel(hotel);
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(HotelResponse::fromHotel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getActiveHotels() {
        return hotelRepository.findByActiveTrue().stream()
                .map(HotelResponse::fromHotel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getHotelsByCity(String city) {
        return hotelRepository.findByCityIgnoreCase(city).stream()
                .map(HotelResponse::fromHotel)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getHotelsByMinStars(Integer stars) {
        return hotelRepository.findByStarsGreaterThanEqual(stars).stream()
                .map(HotelResponse::fromHotel)
                .collect(Collectors.toList());
    }

    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        if (request.getName() != null) hotel.setName(request.getName());
        if (request.getCity() != null) hotel.setCity(request.getCity());
        if (request.getAddress() != null) hotel.setAddress(request.getAddress());
        if (request.getDescription() != null) hotel.setDescription(request.getDescription());
        if (request.getStars() != null) hotel.setStars(request.getStars());
        if (request.getAmenities() != null) hotel.setAmenities(request.getAmenities());

        return HotelResponse.fromHotel(hotelRepository.save(hotel));
    }

    public void deactivateHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        hotel.setActive(false);
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new HotelNotFoundException(id);
        }
        hotelRepository.deleteById(id);
    }
}
