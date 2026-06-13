package com.hotelreservation.hotel.service;

import com.hotelreservation.hotel.dto.RoomRequest;
import com.hotelreservation.hotel.dto.RoomResponse;
import com.hotelreservation.hotel.exception.HotelNotFoundException;
import com.hotelreservation.hotel.exception.RoomNotFoundException;
import com.hotelreservation.hotel.model.Hotel;
import com.hotelreservation.hotel.model.Room;
import com.hotelreservation.hotel.repository.HotelRepository;
import com.hotelreservation.hotel.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    public RoomResponse createRoom(RoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(request.getHotelId()));

        if (roomRepository.existsByHotelIdAndRoomNumber(request.getHotelId(), request.getRoomNumber())) {
            throw new IllegalArgumentException("Room " + request.getRoomNumber() + " already exists in this hotel");
        }

        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .available(true)
                .build();

        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        return RoomResponse.fromRoom(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        return roomRepository.findByHotelId(hotelId).stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAvailableRoomsByHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        return roomRepository.findByHotelIdAndAvailableTrue(hotelId).stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllAvailableRooms() {
        return roomRepository.findByAvailableTrue().stream()
                .map(RoomResponse::fromRoom)
                .collect(Collectors.toList());
    }

    public RoomResponse updateRoomAvailability(Long id, Boolean available) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
        room.setAvailable(available);
        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));

        if (request.getPricePerNight() != null) room.setPricePerNight(request.getPricePerNight());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getType() != null) room.setType(request.getType());

        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException(id);
        }
        roomRepository.deleteById(id);
    }
}
