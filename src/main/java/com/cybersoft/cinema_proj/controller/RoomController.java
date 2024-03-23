package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.RoomDTO;
import com.cybersoft.cinema_proj.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

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
}