package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.ShowTimeDTO;
import com.cybersoft.cinema_proj.entity.*;
import com.cybersoft.cinema_proj.repository.*;
import com.cybersoft.cinema_proj.response.ScheduleRoomResponse;
import com.cybersoft.cinema_proj.response.ShowTimeReponse;
import com.google.gson.Gson;
import jakarta.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ShowTimeService {

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SeatBookingRepository seatBookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    public List<ShowTimeDTO> getAllShowtime(){
        List<ShowTimeEntity> listShowtime = showTimeRepository.getListShowtime();
        List<ShowTimeDTO> listShowtimeDTO = new ArrayList<>();
        for (ShowTimeEntity data : listShowtime){
            ShowTimeDTO showTimeDTO = new ShowTimeDTO();
            BeanUtils.copyProperties(data, showTimeDTO);
            listShowtimeDTO.add(showTimeDTO);
        }
        return listShowtimeDTO;
    }

    public List<ShowTimeDTO> getShowTimesByMovieId(int movieId) {
        List<ShowTimeEntity> showTimeEntities = showTimeRepository.findByMovieId(movieId);
        List<ShowTimeDTO> showTimeDTOs = new ArrayList<>();
        for (ShowTimeEntity showTimeEntity : showTimeEntities) {
            ShowTimeDTO showTimeDTO = new ShowTimeDTO();
            BeanUtils.copyProperties(showTimeEntity, showTimeDTO);
            showTimeDTOs.add(showTimeDTO);
        }
        return showTimeDTOs;
    }

    public ShowTimeDTO getShowTimeDetailsById(int showTimeId) {
        Optional<ShowTimeEntity> showTimeEntityOptional = showTimeRepository.findById(showTimeId);
        if (showTimeEntityOptional.isPresent()) {
            ShowTimeDTO showTimeDTO = new ShowTimeDTO();
            BeanUtils.copyProperties(showTimeEntityOptional.get(), showTimeDTO);
            return showTimeDTO;
        }
        return null;
    }

    public ScheduleRoomResponse findScheduleAndRoomByShowTimeId(int showTimeId) {
        Tuple result = showTimeRepository.findScheduleAndRoomByShowTimeId(showTimeId);
        if (result != null) {
            ScheduleEntity schedule = result.get("schedule", ScheduleEntity.class);
            RoomEntity room = result.get("room", RoomEntity.class);

            ScheduleRoomResponse response = new ScheduleRoomResponse();
            response.setSchedule(schedule);
            response.setRoom(room);
            System.out.println("Response: " + new Gson().toJson(response));  // Log response

            return response;
        }
        return null;
    }

    public LocalDateTime calculateEndTime(int showTimeId) {
        Optional<ShowTimeEntity> optionalShowTime = showTimeRepository.findById(showTimeId);
        if (!optionalShowTime.isPresent()) {
            throw new RuntimeException("ShowTime not found");
        }

        ShowTimeEntity showTime = optionalShowTime.get();
        ScheduleEntity schedule = showTime.getSchedule();
        MovieEntity movie = showTime.getMovie();

        if (schedule == null || movie == null) {
            throw new RuntimeException("Schedule or Movie not found for the ShowTime");
        }

        String startTimeStr = schedule.getStartTime();
        if (startTimeStr == null || startTimeStr.isEmpty()) {
            throw new RuntimeException("Start time is null or empty");
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.now(), startTime);
        LocalDateTime endDateTime = startDateTime.plusMinutes(movie.getDuration());

        showTime.setEndTime(endDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        showTimeRepository.save(showTime);

        return endDateTime;
    }


    public ShowTimeDTO createShowTime(ShowTimeDTO showTimeDTO) {
        Integer roomId = showTimeDTO.getRoom().getId();
        Optional<RoomEntity> roomEntityOptional = roomRepository.findById(roomId);
        if (roomEntityOptional.isEmpty()) {
            throw new RuntimeException("Room with ID " + roomId + " does not exist.");
        }
        RoomEntity roomEntity = roomEntityOptional.get();
        if (roomEntity.isStatus()) {
            throw new RuntimeException("Cannot schedule show time for room with ID " + roomId + " because it is already active.");
        }

        Integer movieId = showTimeDTO.getMovie().getId();
        Optional<MovieEntity> movieEntityOptional = movieRepository.findById(movieId);
        if (movieEntityOptional.isEmpty()) {
            throw new RuntimeException("Movie with ID " + movieId + " does not exist.");
        }

        Integer scheduleId = showTimeDTO.getSchedule().getId();
        Optional<ScheduleEntity> scheduleEntityOptional = scheduleRepository.findById(scheduleId);
        if (scheduleEntityOptional.isEmpty()) {
            throw new RuntimeException("Schedule with ID " + scheduleId + " does not exist.");
        }

        ShowTimeEntity showTimeEntity = new ShowTimeEntity();
        showTimeEntity.setMovie(movieEntityOptional.get());
        showTimeEntity.setSchedule(scheduleEntityOptional.get());
        showTimeEntity.setRoom(roomEntity);
        showTimeEntity.setStatus(true);

        ShowTimeEntity savedShowTime = showTimeRepository.save(showTimeEntity);
        seatBookingRepository.insertSeatBooking(Long.valueOf(savedShowTime.getId()), roomId);

//        roomService.updateRoomStatus(Long.valueOf(roomId));
        ShowTimeDTO createdShowTimeDTO = new ShowTimeDTO();
        createdShowTimeDTO.setId(savedShowTime.getId());
        createdShowTimeDTO.setMovie(savedShowTime.getMovie());
        createdShowTimeDTO.setSchedule(savedShowTime.getSchedule());
        createdShowTimeDTO.setRoom(savedShowTime.getRoom());
        createdShowTimeDTO.setEndTime(savedShowTime.getEndTime());

        return createdShowTimeDTO;
    }

    public List<ShowTimeReponse> getShowTimeDetails() {
        List<Tuple> results = showTimeRepository.getShowTimeDetails();
        List<ShowTimeReponse> showTimeResponseDTOList = new ArrayList<>();
        for (Tuple tuple : results) {
            int showTimeId = (int) tuple.get("showtimeId");
            String startTime = tuple.get("startTime", String.class);
            String endTime = tuple.get("endTime", String.class);
            String showDate = tuple.get("showDate", String.class);

            ShowTimeEntity showTimeDTO = new ShowTimeEntity();
            showTimeDTO.setId(showTimeId);
            showTimeDTO.setEndTime(endTime);

            ScheduleEntity scheduleDTO = new ScheduleEntity();
            scheduleDTO.setStartTime(startTime);

            DayTimeEntity dayTimeDTO = new DayTimeEntity();
            dayTimeDTO.setDay_time(showDate);

            ShowTimeReponse showTimeResponseDTO = new ShowTimeReponse();
            showTimeResponseDTO.setShowtime(showTimeDTO);
            showTimeResponseDTO.setSchedule(scheduleDTO);
            showTimeResponseDTO.setDaytime(dayTimeDTO);

            showTimeResponseDTOList.add(showTimeResponseDTO);
        }
        return showTimeResponseDTOList;
    }

    public void checkAndProcessExpiredShowTimes() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        String currentDate = LocalDateTime.now().toLocalDate().format(dateFormatter);
        String currentTime = LocalDateTime.now().toLocalTime().format(timeFormatter);

        System.out.println("Checking for expired show times... Current Date: " + currentDate + ", Current Time: " + currentTime);

        List<ShowTimeEntity> expiredShowTimes = showTimeRepository.findExpiredShowTimes(currentDate, currentTime);
        for (ShowTimeEntity expiredShowTime : expiredShowTimes) {
            expiredShowTime.setStatus(false);
            showTimeRepository.save(expiredShowTime);
        }
    }
}