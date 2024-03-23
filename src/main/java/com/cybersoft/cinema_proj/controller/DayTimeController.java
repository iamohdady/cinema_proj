package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.DayTimeDTO;
import com.cybersoft.cinema_proj.dto.ShowTimeDTO;
import com.cybersoft.cinema_proj.response.DaytimeResponse;
import com.cybersoft.cinema_proj.service.DayTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daytimes")
public class DayTimeController {

    private final DayTimeService dayTimeService;

    @Autowired
    public DayTimeController(DayTimeService dayTimeService) {
        this.dayTimeService = dayTimeService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDayTimes() {
        List<DayTimeDTO> dayTimes = dayTimeService.getAllDayTimes();
        return new ResponseEntity<>(dayTimes, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDayTime(@RequestBody DayTimeDTO dayTimeDTO) {
        DayTimeDTO savedDayTimeDTO = dayTimeService.addDayTime(dayTimeDTO);
        if (savedDayTimeDTO != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDayTimeDTO);
        } else {
            return new ResponseEntity<>("Failed to add movie", HttpStatus.INTERNAL_SERVER_ERROR);
        }    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateDayTime(@PathVariable int id, @ModelAttribute DayTimeDTO dayTimeDTO) {
        DayTimeDTO updatedDayTimeDTO = dayTimeService.getDayTimeById(id);
        if (updatedDayTimeDTO == null) {
            return new ResponseEntity<>("DayTime not found", HttpStatus.NOT_FOUND);
        }
        DayTimeDTO updateDayTime = dayTimeService.updateDayTime(dayTimeDTO);
        if (updateDayTime != null) {
            return new ResponseEntity<>("DayTime updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update daytime", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteDayTime(@PathVariable int id) {
        dayTimeService.deleteDayTime(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDayTime(@RequestBody DayTimeDTO dayTimeDTO) {
        DayTimeDTO createdDayTimeDTO = dayTimeService.createDayTime(dayTimeDTO);
        return ResponseEntity.ok().body(createdDayTimeDTO);
    }


    @GetMapping("/details/{id}")
    public ResponseEntity<List<DaytimeResponse>> getDaytimeDetails(@PathVariable("id") int daytimeId) {
        List<DaytimeResponse> details = dayTimeService.findRoomMovieScheduleShowTimeByDaytimeId(daytimeId);
        return ResponseEntity.ok(details);
    }


}
