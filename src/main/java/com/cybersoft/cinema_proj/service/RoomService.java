package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.RoomDTO;
import com.cybersoft.cinema_proj.entity.RoomEntity;
import com.cybersoft.cinema_proj.entity.SeatEntity;
import com.cybersoft.cinema_proj.repository.RoomRepository;
import com.cybersoft.cinema_proj.repository.SeatRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<RoomDTO> getAllRooms() {
        List<RoomEntity> roomEntities = roomRepository.findAll();
        List<RoomDTO> roomDTOs = new ArrayList<>();
        for (RoomEntity roomEntity : roomEntities) {
            RoomDTO roomDTO = new RoomDTO();
            BeanUtils.copyProperties(roomEntity, roomDTO);
            roomDTOs.add(roomDTO);
        }
        return roomDTOs;
    }

    public RoomDTO addRoom(RoomDTO roomDTO) {
        // Tạo một đối tượng RoomEntity từ RoomDTO
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomDTO.getName());
        roomEntity.setCapacity(roomDTO.getCapacity());
        roomEntity.setDescription(roomDTO.getDescription());
        roomEntity.setRate("Phòng thường"); // Thêm rate là "Phòng thường"
        roomEntity.setStatus(false); // Thêm status có giá trị là false từ DTO

        // Lưu phòng mới vào cơ sở dữ liệu
        RoomEntity savedRoom = roomRepository.save(roomEntity);

        // Tạo số lượng ghế tương ứng với capacity và lưu chúng vào cơ sở dữ liệu
        for (int i = 1; i <= roomDTO.getCapacity(); i++) {
            SeatEntity seat = new SeatEntity();
            seat.setName(generateSeatName(i));
            seat.setBooked(false);
            seat.setRoom(savedRoom);
            seatRepository.save(seat);
        }

        // Cập nhật lại thông tin phòng nếu cần
        BeanUtils.copyProperties(savedRoom, roomDTO);
        return roomDTO;
    }


    // Hàm tạo tên ghế
    private String generateSeatName(int seatNumber) {
        char letter = (char) ('A' + (seatNumber - 1) / 10); // Chuyển số thành ký tự từ A đến Z
        int number = seatNumber % 10 == 0 ? 10 : seatNumber % 10; // Lấy số từ 1 đến 10
        return String.valueOf(letter) + number; // Kết hợp ký tự và số
    }

    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Optional<RoomEntity> optionalRoom = Optional.ofNullable(roomRepository.findById(id));
        if (optionalRoom.isPresent()) {
            RoomEntity roomEntity = optionalRoom.get();
            int oldCapacity = roomEntity.getCapacity();

            roomEntity.setName(roomDTO.getName());
            roomEntity.setCapacity(roomDTO.getCapacity());
            roomEntity.setDescription(roomDTO.getDescription());
            roomEntity.setRate(roomDTO.getRate());
            roomEntity.setStatus(roomDTO.isStatus()); // Update status from DTO

            RoomEntity updatedRoom = roomRepository.save(roomEntity);

            if (roomDTO.getCapacity() > oldCapacity) {
                // Thêm ghế mới nếu capacity mới lớn hơn capacity cũ
                for (int i = oldCapacity + 1; i <= roomDTO.getCapacity(); i++) {
                    SeatEntity seat = new SeatEntity();
                    seat.setName(generateSeatName(i));
                    seat.setBooked(false);
                    seat.setRoom(updatedRoom);
                    seatRepository.save(seat);
                }
            } else if (roomDTO.getCapacity() < oldCapacity) {
                // Xóa ghế nếu capacity mới nhỏ hơn capacity cũ
                List<SeatEntity> seats = seatRepository.findByRoomId(id);
                for (int i = oldCapacity; i > roomDTO.getCapacity(); i--) {
                    SeatEntity seat = seats.get(i - 1);
                    seatRepository.delete(seat);
                }
            }

            RoomDTO resultDTO = new RoomDTO();
            BeanUtils.copyProperties(updatedRoom, resultDTO);

            return resultDTO;
        } else {
            throw new RuntimeException("Room not found with id " + id);
        }
    }

    public void deleteRoom(Long id) {
        Optional<RoomEntity> optionalRoom = Optional.ofNullable(roomRepository.findById(id));
        if (optionalRoom.isPresent()) {
            RoomEntity roomEntity = optionalRoom.get();
            List<SeatEntity> seats = seatRepository.findByRoomId(id);
            seatRepository.deleteAll(seats);
            roomRepository.delete(roomEntity);
        } else {
            throw new RuntimeException("Room not found with id " + id);
        }
    }

    public RoomDTO getRoomById(Long id) {
        Optional<RoomEntity> roomEntityOptional = Optional.ofNullable(roomRepository.findById(id));
        if (roomEntityOptional.isPresent()) {
            RoomEntity roomEntity = roomEntityOptional.get();
            RoomDTO roomDTO = new RoomDTO();
            BeanUtils.copyProperties(roomEntity, roomDTO);
            return roomDTO;
        }
        return null;
    }

    public void updateRoomStatus(Long roomId) {
        Optional<RoomEntity> optionalRoom = Optional.ofNullable(roomRepository.findById(roomId));
        if (optionalRoom.isPresent()) {
            RoomEntity roomEntity = optionalRoom.get();
            roomEntity.setStatus(true);
            roomRepository.save(roomEntity);
        } else {
            throw new RuntimeException("Room not found with id " + roomId);
        }
    }

    public void updateStatus(Long roomId, boolean status) {
        Optional<RoomEntity> optionalRoom = Optional.ofNullable(roomRepository.findById(roomId));
        if (optionalRoom.isPresent()) {
            RoomEntity roomEntity = optionalRoom.get();
            roomEntity.setStatus(status);
            roomRepository.save(roomEntity);
        } else {
            throw new RuntimeException("Room not found with id " + roomId);
        }
    }

}
