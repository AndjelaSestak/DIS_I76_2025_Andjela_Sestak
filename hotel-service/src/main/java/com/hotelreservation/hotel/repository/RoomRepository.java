package com.hotelreservation.hotel.repository;

import com.hotelreservation.hotel.model.Room;
import com.hotelreservation.hotel.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    List<Room> findByHotelIdAndAvailableTrue(Long hotelId);

    List<Room> findByHotelIdAndType(Long hotelId, RoomType type);

    List<Room> findByAvailableTrue();

    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
}
