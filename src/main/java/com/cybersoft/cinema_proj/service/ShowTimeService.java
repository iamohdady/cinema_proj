package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.MovieDTO;
import com.cybersoft.cinema_proj.dto.RoomDTO;
import com.cybersoft.cinema_proj.dto.ScheduleDTO;
import com.cybersoft.cinema_proj.dto.ShowTimeDTO;
import com.cybersoft.cinema_proj.entity.*;
import com.cybersoft.cinema_proj.repository.*;
import com.cybersoft.cinema_proj.response.DayScheduleReponse;
import com.cybersoft.cinema_proj.response.DaytimeResponse;
import com.cybersoft.cinema_proj.response.ScheduleRoomResponse;
import com.cybersoft.cinema_proj.response.ShowTimeReponse;
import com.google.gson.Gson;
import jakarta.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private RoomBookingRepository roomBookingRepository;

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

        int showTimeId = savedShowTime.getId();
        roomBookingRepository.insertRoomBooking(showTimeId, roomId, true);  // Giả sử bạn có phương thức updateRoomBooking trong repository

//        roomService.updateRoomBookingStatus(Long.valueOf(roomId));
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

            RoomEntity roomEntity = expiredShowTime.getRoom();
            if (roomEntity != null) {
                roomEntity.setStatus(false);
                roomRepository.save(roomEntity);
            }
        }
    }

    public List<RoomDTO> getAvailableRooms(String startTime) {
        List<RoomEntity> availableRooms = roomRepository.findAvailableRooms(startTime);
        return availableRooms.stream().map(this::convertToRoomDTO).collect(Collectors.toList());
    }

    private RoomDTO convertToRoomDTO(RoomEntity roomEntity) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(roomEntity.getId());
        roomDTO.setName(roomEntity.getName());
        roomDTO.setStatus(roomEntity.isStatus());
        // Map thêm các trường khác nếu cần
        return roomDTO;
    }

//    public List<DayScheduleReponse> getTime() {
//        List<Tuple> result = showTimeRepository.getTime();
//        if (result != null) {
//            ScheduleEntity schedule = result.get("schedule", ScheduleEntity.class);
//            DayTimeEntity room = result.get("daytime", DayTimeEntity.class);
//
//            DayScheduleReponse response = new DayScheduleReponse();
//            response.setSchedule(schedule);
//            response.setDaytime(room);
//            System.out.println("Response: " + new Gson().toJson(response));  // Log response
//
//            return (List<DayScheduleReponse>) response;
//        }
//        return null;
//
//    }

    public String calculateEndTime(int movieId, String startTime) {
        MovieEntity movie = movieRepository.findById(movieId);

        // Assuming startTime is in format "HH:mm" and duration is in minutes
        LocalTime startTimeLocal = LocalTime.parse(startTime);
        LocalTime endTimeLocal = startTimeLocal.plusMinutes(movie.getDuration());

        return endTimeLocal.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

//    public List<RoomEntity> getRoomsByDayTimeAndStartTime(int dayTimeId, String startTime) {
//        return showTimeRepository.findRoomsByDayTimeAndStartTime(dayTimeId, startTime);
//    }

    public List<Object[]> getAllShowTimeDetails() {
        return showTimeRepository.findAllShowTimeDetails();
    }

    /**
     * Phương thức tìm kiếm danh sách các phòng có sẵn để xếp lịch chiếu phim dựa trên điều kiện:
     * 1. Các phòng chưa được xếp trong showtime.
     * 2. Các phòng đã được xếp nhưng endTime của showtime nhỏ hơn startTime mới.
     * 3. startTime mới không nằm trong khoảng thời gian startTime và endTime của các showtime đã có.
     * 4. startTime mới lớn hơn endTime của các showtime đã có.
     *
     * @param movieId   ID của phim được chọn để tính endTime
     * @param startTime Thời gian bắt đầu mới để tính endTime
     * @return Danh sách các phòng thỏa mãn điều kiện
     */
    public List<RoomEntity> findAvailableRooms(int movieId, String startTime) {
        // Tính toán endTime dựa trên movieId và startTime
        String endTime = calculateEndTime(movieId, startTime);

        // Lấy danh sách chi tiết showtime từ database
        List<Object[]> showTimesDetails = showTimeRepository.findAllShowTimeDetails();

        // Chuyển đổi startTime và endTime sang LocalTime để so sánh
        LocalTime newStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime newEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Lọc các phòng dựa trên các điều kiện
        List<RoomEntity> availableRooms = roomRepository.findAll().stream()
            .filter(room -> {
                // Kiểm tra nếu phòng chưa được xếp lịch chiếu
                boolean roomNotScheduled = showTimesDetails.stream()
                    .noneMatch(showtime -> room.getId() == ((BigInteger) showtime[3]).intValue());

                // Kiểm tra nếu phòng đã được xếp nhưng endTime < newStartTime
                boolean roomScheduledEndTimeBeforeNewStartTime = showTimesDetails.stream()
                    .anyMatch(showtime -> {
                        LocalTime scheduledEndTime = LocalTime.parse((CharSequence) showtime[2],
                            DateTimeFormatter.ofPattern("HH:mm:ss"));
                        return room.getId() == ((BigInteger) showtime[3]).intValue()
                            && scheduledEndTime.isBefore(newStartTime);
                    });

                // Kiểm tra nếu newStartTime không nằm trong khoảng thời gian của bất kỳ showtime đã có
                boolean startTimeNotBetweenExistingTimes = showTimesDetails.stream()
                    .noneMatch(showtime -> {
                        LocalTime scheduledStartTime = LocalTime.parse((CharSequence) showtime[1],
                            DateTimeFormatter.ofPattern("HH:mm:ss"));
                        LocalTime scheduledEndTime = LocalTime.parse((CharSequence) showtime[2],
                            DateTimeFormatter.ofPattern("HH:mm:ss"));
                        return room.getId() == ((BigInteger) showtime[3]).intValue()
                            && !newStartTime.isAfter(scheduledEndTime)
                            && !newStartTime.isBefore(scheduledStartTime);
                    });

                // Kiểm tra nếu newStartTime lớn hơn endTime của bất kỳ showtime đã có
                boolean startTimeAfterExistingEndTime = showTimesDetails.stream()
                    .anyMatch(showtime -> {
                        LocalTime scheduledEndTime = LocalTime.parse((CharSequence) showtime[2],
                            DateTimeFormatter.ofPattern("HH:mm:ss"));
                        return room.getId() == ((BigInteger) showtime[3]).intValue()
                            && newStartTime.isAfter(scheduledEndTime);
                    });

                // Bao gồm phòng nếu phòng thỏa mãn bất kỳ điều kiện nào
                return roomNotScheduled || roomScheduledEndTimeBeforeNewStartTime
                    || (startTimeNotBetweenExistingTimes && startTimeAfterExistingEndTime);
            })
            .collect(Collectors.toList());

        return availableRooms;
    }


}