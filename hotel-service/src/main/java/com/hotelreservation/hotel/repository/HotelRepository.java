package com.hotelreservation.hotel.repository;

import com.hotelreservation.hotel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCity(String city);

    List<Hotel> findByCityIgnoreCase(String city);

    List<Hotel> findByActiveTrue();

    List<Hotel> findByStarsGreaterThanEqual(Integer stars);

    boolean existsByNameAndCity(String name, String city);
}
