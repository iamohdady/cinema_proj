package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.ShowTimeDTO;
import com.cybersoft.cinema_proj.response.CaculateEndTime;
import com.cybersoft.cinema_proj.response.DayScheduleReponse;
import com.cybersoft.cinema_proj.response.ScheduleRoomResponse;
import com.cybersoft.cinema_proj.response.ShowTimeReponse;
import com.cybersoft.cinema_proj.service.ShowTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/showtimes")
public class ShowTimeController {

    @Autowired
    private ShowTimeService showTimeService;

    @GetMapping("/all")
    public ResponseEntity<?> getMovie(){
        List<ShowTimeDTO> listShowtime = showTimeService.getAllShowtime();
        return new ResponseEntity<>(listShowtime, HttpStatus.OK);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getShowTimesByMovieId(@PathVariable int movieId) {
        List<ShowTimeDTO> listRoom = showTimeService.getShowTimesByMovieId(movieId);
        return new ResponseEntity<>(listRoom, HttpStatus.OK);
    }

    @GetMapping("/{showTimeId}")
    public ResponseEntity<ShowTimeDTO> getShowTimeById(@PathVariable int showTimeId) {
        ShowTimeDTO showTime = showTimeService.getShowTimeDetailsById(showTimeId);
        if (showTime != null) {
            return ResponseEntity.ok().body(showTime);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/details/{showTimeId}")
    public ResponseEntity<?> getShowTimeDetails(@PathVariable int showTimeId) {
        ScheduleRoomResponse showTime = showTimeService.findScheduleAndRoomByShowTimeId(showTimeId);
        if (showTime != null) {
            return ResponseEntity.ok().body(showTime);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createShowTime(@RequestBody ShowTimeDTO showTimeDTO) {
        ShowTimeDTO createdShowTime = showTimeService.createShowTime(showTimeDTO);

        LocalDateTime endTime = showTimeService.calculateEndTime(createdShowTime.getId());
        createdShowTime.setEndTime(String.valueOf(endTime));

        return new ResponseEntity<>(createdShowTime, HttpStatus.CREATED);
    }

    @GetMapping("/endtime/{showTimeId}")
    public ResponseEntity<String> getEndTime(@PathVariable int showTimeId) {
        try {
            LocalDateTime endTime = showTimeService.calculateEndTime(showTimeId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedEndTime = endTime.format(formatter);
            return ResponseEntity.ok().body(formattedEndTime);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/details")
    public ResponseEntity<List<?>> getShowTimeDetails() {
        List<ShowTimeReponse> showTimeDetails = showTimeService.getShowTimeDetails();
        return ResponseEntity.ok(showTimeDetails);
    }

    @Scheduled(fixedRate = 30000)
    public void processExpiredShowTimes() {
        showTimeService.checkAndProcessExpiredShowTimes();
    }



    @PostMapping("/calculate-end-time")
    public ResponseEntity<String> calculateEndTime(@RequestBody CaculateEndTime request) {
        String endTime = showTimeService.calculateEndTime(request.getMovieId(), request.getStartTime());
        return ResponseEntity.ok(endTime);
    }

    @GetMapping("/ifo")
    public List<Object[]> getAllShowTimeDetails() {
        return showTimeService.getAllShowTimeDetails();
    }
}

//    @GetMapping("/time")
//    public ResponseEntity<List<DayScheduleReponse>> getTime() {
//        List<DayScheduleReponse> showTimeDTOs = showTimeService.getTime();
//        return new ResponseEntity<>(showTimeDTOs, HttpStatus.OK);
//    }

