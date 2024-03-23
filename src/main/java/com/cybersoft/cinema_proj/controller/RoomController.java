package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.RoomDTO;
import com.cybersoft.cinema_proj.entity.RoomEntity;
import com.cybersoft.cinema_proj.response.RoomBookingResponse;
import com.cybersoft.cinema_proj.service.RoomService;
import com.cybersoft.cinema_proj.service.ShowTimeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ShowTimeService showTimeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllRoom() {
        List<RoomDTO> listMovie = roomService.getAllRooms();
        return new ResponseEntity<>(listMovie, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRoom(@RequestBody RoomDTO roomDTO) {
        RoomDTO addedRoom = roomService.addRoom(roomDTO);
        return new ResponseEntity<>(addedRoom, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomDTO roomDTO) {
        RoomDTO updatedRoom = roomService.updateRoom(id, roomDTO);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable("id") Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable("id") Long id) {
        RoomDTO roomDTO = roomService.getRoomById(id);
        if (roomDTO != null) {
            return new ResponseEntity<>(roomDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Room not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<?> updateRoomStatus(@PathVariable("id") Long id, @RequestParam("status") boolean status) {
        roomService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/room_booking/{roomBookingId}")
    public RoomBookingResponse getRoomBookingDetailsById(@PathVariable int roomBookingId) {
        return roomService.getRoomBookingDetailsById(roomBookingId);
    }

    @GetMapping("/available-rooms")
    public List<RoomDTO> getAvailableRooms(@RequestParam String startTime) {
        return showTimeService.getAvailableRooms(startTime);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomEntity>> getAvailableRoomsByDayTime(
        @RequestParam("dayTimeId") int dayTimeId) {

        List<RoomEntity> rooms = roomService.getAvailableRoomsByDayTime(dayTimeId);

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomEntity>> getRoomsByDayTimeAndStartTime(
        @RequestParam("dayTimeId") int dayTimeId,
        @RequestParam("startTime") String startTime) {

        List<RoomEntity> rooms = roomService.getRoomsByDayTimeAndStartTime(dayTimeId, startTime);

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/list-available")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
        @RequestParam("movieId") int movieId,
        @RequestParam("startTime") String startTime) {

        // Gọi phương thức của service để lấy danh sách phòng có sẵn dựa trên movieId và startTime
        List<RoomEntity> availableRooms = roomService.findAvailableRooms(movieId, startTime);

        // Chuyển đổi từ RoomEntity sang RoomDTO (nếu cần)
        List<RoomDTO> roomDTOs = availableRooms.stream()
            .map(room -> {
                RoomDTO roomDTO = new RoomDTO();
                BeanUtils.copyProperties(room, roomDTO);
                return roomDTO;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(roomDTOs);
    }
    @GetMapping("/with-showtimes")
    public List<RoomEntity> getAllRoomsWithShowTimes() {
        return roomService.getAllRoomsWithShowTimes();
    }

    @GetMapping("/scheduled-before")
    public List<RoomEntity> getRoomsScheduledBefore(
        @RequestParam String startTime
    ) {
        return roomService.findRoomsScheduledBefore(startTime);
    }

    @GetMapping("/schedule-start-time-not-between-showtime")
    public List<RoomEntity> getRoomsByScheduleStartTimeNotBetweenShowTime(
        @RequestParam String startTime
    ) {
        return roomService.findRoomsByScheduleStartTimeNotBetweenShowTime(startTime);
    }

    @GetMapping("/schedule-after")
    public List<RoomEntity> getShowTimesBetween(
        @RequestParam int movieId,
        @RequestParam String startTime
    ) {
        String endTime = showTimeService.calculateEndTime(movieId, startTime);
         return roomService.findShowTimesBetween(endTime);
    }

//    @GetMapping("/rooms-for-schedule")
//    public List<RoomEntity> getRoomsForSchedule(
//        @RequestParam String startTime
//    ) {
//        return roomService.findRoomsScheduledBeforeAndNotBetween(startTime);
//    }

//    @GetMapping("/rooms-for-schedule")
//    public List<RoomEntity> getRoomsForSchedule(
//        @RequestParam String startTime
//    ) {
//        return roomService.findRoomsByCombinedConditions(startTime);
//    }

    @GetMapping("/rooms-for-schedule")
    public List<RoomEntity> getRoomsForSchedule(
        @RequestParam String startTime,
        @RequestParam int daytimeId
    ) {
        return roomService.findRoomsByCombinedConditions(startTime, daytimeId);
    }
}