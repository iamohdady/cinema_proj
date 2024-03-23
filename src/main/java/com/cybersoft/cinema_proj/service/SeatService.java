package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.SeatDTO;
import com.cybersoft.cinema_proj.entity.SeatEntity;
import com.cybersoft.cinema_proj.repository.SeatBookingRepository;
import com.cybersoft.cinema_proj.repository.SeatRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SeatBookingRepository seatBookingRepository;

    public List<SeatDTO> getSeatsByRoomId(Long roomId) {
        List<SeatEntity> seatEntities = seatRepository.findByRoomId(roomId);
        List<SeatDTO> seatDTOs = new ArrayList<>();
        for (SeatEntity seatEntity : seatEntities) {
            SeatDTO seatDTO = new SeatDTO();
            BeanUtils.copyProperties(seatEntity, seatDTO);
            seatDTOs.add(seatDTO);
        }
        return seatDTOs;
    }

    public List<SeatDTO> getSeatsByShowtime(Long showtimeId) {
        List<Object[]> seatEntities = seatBookingRepository.getListSeat(showtimeId);
        List<SeatDTO> seatDTOs = new ArrayList<>();
        for (Object[] result : seatEntities) { // Thay vì results, bạn cần sử dụng seatEntities ở đây
            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setId((Integer) result[0]);
            seatDTO.setName((String) result[1]);
            seatDTO.setBooked((Boolean) result[2]);
            seatDTOs.add(seatDTO);
        }

        return seatDTOs;
    }


    public SeatDTO getSeatDetailsById(int seatId) {
        Optional<SeatEntity> seatEntityOptional = seatRepository.findById(seatId);
        if (seatEntityOptional.isPresent()) {
            SeatDTO seatDTO = new SeatDTO();
            BeanUtils.copyProperties(seatEntityOptional.get(), seatDTO);
            return seatDTO;
        }
        return null;
    }

    public void updateSeatStatus(int seatId, boolean isBooked) {
        Optional<SeatEntity> seatOptional = seatRepository.findById(seatId);
        if (seatOptional.isPresent()) {
            SeatEntity seat = seatOptional.get();
            seat.setBooked(isBooked);
            seatRepository.save(seat);
        } else {
            throw new RuntimeException("Ghế không tồn tại với ID: " + seatId);
        }
    }

    public void updateAllSeatsStatusInRoomToAvailable(Integer roomId) {
        List<SeatEntity> seats = seatRepository.findByRoomId(Long.valueOf(roomId));
        for (SeatEntity seat : seats) {
            seat.setBooked(false); // Đây là trạng thái mặc định khi ghế chưa được đặt
        }
        seatRepository.saveAll(seats);
    }

    public void resetSeatStatus() {
        // Lấy tất cả danh sách ghế từ cơ sở dữ liệu
        List<SeatEntity> allSeats = seatRepository.findAll();

        // Lặp qua từng ghế và cập nhật trạng thái của chúng
        for (SeatEntity seat : allSeats) {
            seat.setBooked(false); // Chuyển trạng thái ghế về chưa được đặt chỗ
            seatRepository.save(seat); // Lưu thay đổi vào cơ sở dữ liệu
        }
    }

    public void cancelSeats(List<Long> seatIds) {
        for (Long seatId : seatIds) {
            // Gọi phương thức hủy ghế trong repository hoặc làm thao tác cần thiết
            seatRepository.cancelSeat(Collections.singletonList(seatId));
        }
    }

}
