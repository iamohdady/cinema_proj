package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.SeatDTO;
import com.cybersoft.cinema_proj.request.CancelSeatsRequest;
import com.cybersoft.cinema_proj.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getSeatsByRoomId(@PathVariable Long roomId) {
        List<SeatDTO> listRoom = seatService.getSeatsByRoomId(roomId);
        return new ResponseEntity<>(listRoom, HttpStatus.OK);
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<?> getSeatsByShowtimeId(@PathVariable Long showtimeId) {
        List<SeatDTO> listRoom = seatService.getSeatsByShowtime(showtimeId);
        return new ResponseEntity<>(listRoom, HttpStatus.OK);
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<?> getShowTimeById(@PathVariable int seatId) {
        SeatDTO showTime = seatService.getSeatDetailsById(seatId);
        if (showTime != null) {
            return ResponseEntity.ok().body(showTime);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/status/{seatId}")
    public ResponseEntity<String> updateSeatStatus(@PathVariable Long seatId, @RequestParam boolean status) {
        seatService.updateSeatStatus(Math.toIntExact(seatId), status);
        return ResponseEntity.ok().body("Trạng thái của ghế đã được cập nhật.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetSeatStatus() {
        seatService.resetSeatStatus();
        return ResponseEntity.ok().body("Tất cả trạng thái của các ghế đã được reset về chưa được đặt chỗ.");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelSeats(@RequestBody CancelSeatsRequest request) {
        List<Long> seatIds = request.getSeatIds();
        seatService.cancelSeats(seatIds);
        return ResponseEntity.ok().body("Các ghế đã được hủy thành công.");
    }
}