package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.ScheduleDTO;
import com.cybersoft.cinema_proj.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/all")
    public ResponseEntity<?> getSchedule(){
        List<ScheduleDTO> listSchedule = scheduleService.getAll();
        return new ResponseEntity<>(listSchedule, HttpStatus.OK);
    }

    @GetMapping("/daytime/{daytimeId}")
    public ResponseEntity<?> getDayTimeByDaytimeId(@PathVariable int daytimeId) {
        List<ScheduleDTO> listSchedule = scheduleService.getScheduleDaytimeId(daytimeId);
        return new ResponseEntity<>(listSchedule, HttpStatus.OK);
    }

    @GetMapping("/daytime/all")
    public ResponseEntity<?> getMovie(){
        List<ScheduleDTO> listSchedule = scheduleService.getAllSchedule();
        return new ResponseEntity<>(listSchedule, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO createdScheduleDTO = scheduleService.createSchedule(scheduleDTO);
        return new ResponseEntity<>(createdScheduleDTO, HttpStatus.CREATED);
    }
}