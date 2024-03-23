package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.*;
import com.cybersoft.cinema_proj.entity.*;
import com.cybersoft.cinema_proj.repository.*;
import com.cybersoft.cinema_proj.response.RoomBookingResponse;
import com.cybersoft.cinema_proj.response.ShowtimeDetailsDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomBookingRepository roomBookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private MovieRepository movieRepository;

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

    public void updateRoomBookingStatus(Long roomId) {
        Optional<RoomBookingEntity> optionalRoom = Optional.ofNullable(roomBookingRepository.findById(roomId));
        if (optionalRoom.isPresent()) {
            RoomBookingEntity roomBookingEntity = optionalRoom.get();
            roomBookingEntity.setBooked(true);
            roomBookingRepository.save(roomBookingEntity);
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

    public RoomBookingResponse getRoomBookingDetailsById(int roomBookingId) {
        RoomBookingEntity roomBooking = roomBookingRepository.findById(roomBookingId)
            .orElseThrow(() -> new RuntimeException("Room Booking not found with id: " + roomBookingId));

        // Lấy thông tin từ RoomBookingEntity
        ShowTimeEntity showtime = roomBooking.getShowtime_id();
        RoomEntity room = roomBooking.getRoom_id();

        // Lấy thông tin từ ShowTimeEntity
        ScheduleEntity schedule = showtime.getSchedule();
        MovieEntity movie = showtime.getMovie();

        // Lấy thông tin từ ScheduleEntity
        DayTimeEntity daytime = schedule.getDay_time();

        // Tạo DTO và ánh xạ các thông tin
        RoomBookingResponse detailsDTO = new RoomBookingResponse();
        detailsDTO.setRoomBookingId(roomBooking.getId());
        detailsDTO.setShowtime(convertToShowTimeDTO(showtime));
        detailsDTO.setRoom(convertToRoomDTO(room));
        detailsDTO.setSchedule(convertToScheduleDTO(schedule));
        detailsDTO.setDaytime(convertToDayTimeDTO(daytime));
        detailsDTO.setMovie(convertToMovieDTO(movie));

        return detailsDTO;
    }

    private ShowTimeDTO convertToShowTimeDTO(ShowTimeEntity showTimeEntity) {
        ShowTimeDTO showTimeDTO = new ShowTimeDTO();
        BeanUtils.copyProperties(showTimeEntity, showTimeDTO);
        return showTimeDTO;
    }

    private RoomDTO convertToRoomDTO(RoomEntity roomEntity) {
        RoomDTO roomDTO = new RoomDTO();
        BeanUtils.copyProperties(roomEntity, roomDTO);
        return roomDTO;
    }

    private ScheduleDTO convertToScheduleDTO(ScheduleEntity scheduleEntity) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(scheduleEntity, scheduleDTO);
        return scheduleDTO;
    }

    private DayTimeDTO convertToDayTimeDTO(DayTimeEntity dayTimeEntity) {
        DayTimeDTO dayTimeDTO = new DayTimeDTO();
        BeanUtils.copyProperties(dayTimeEntity, dayTimeDTO);
        return dayTimeDTO;
    }

    private MovieDTO convertToMovieDTO(MovieEntity movieEntity) {
        MovieDTO movieDTO = new MovieDTO();
        BeanUtils.copyProperties(movieEntity, movieDTO);
        return movieDTO;
    }

    public List<RoomEntity> getAvailableRoomsByDayTime(int dayTimeId) {
        return roomRepository.findAvailableRoomsByDayTime(dayTimeId);
    }

    public List<RoomEntity> getRoomsByDayTimeAndStartTime(int dayTimeId, String startTime) {
        return roomRepository.findRoomsByDayTimeAndStartTime(dayTimeId, startTime);
    }

    public List<RoomEntity> findAvailableRooms(int movieId, String startTime) {
        String endTime = calculateEndTime(movieId, startTime);

        // Parse startTime và endTime sang LocalTime
        LocalTime newStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime newEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Lấy danh sách chi tiết showtime từ database và chuyển đổi từ List<Object[]> sang List<ShowtimeDetailsDTO>
        List<ShowtimeDetailsDTO> showTimesDetails = convertToShowtimeDetails(showTimeRepository.findAllShowTimeDetails());

        List<RoomEntity> availableRooms = new ArrayList<>();

        // Lặp qua danh sách phòng và kiểm tra điều kiện
        for (RoomEntity room : roomRepository.findAll()) {
            if (isRoomAvailable(room, showTimesDetails, newStartTime, newEndTime)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    // Hàm chuyển đổi từ List<Object[]> sang List<ShowtimeDetailsDTO>
    private List<ShowtimeDetailsDTO> convertToShowtimeDetails(List<Object[]> showTimeObjects) {
        return showTimeObjects.stream()
            .map(showtime -> {
                int roomId = (Integer) showtime[3];
                String startTime = (String) showtime[1];
                String endTime = (String) showtime[2];
                return new ShowtimeDetailsDTO(roomId, startTime, endTime);
            })
            .collect(Collectors.toList());
    }

    // Hàm kiểm tra sự sẵn có của phòng
    private boolean isRoomAvailable(RoomEntity room, List<ShowtimeDetailsDTO> showTimesDetails, LocalTime newStartTime, LocalTime newEndTime) {
        for (ShowtimeDetailsDTO showtime : showTimesDetails) {
            if (room.getId() == showtime.getRoomId()) {
                LocalTime scheduledStartTime = LocalTime.parse(showtime.getStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                LocalTime scheduledEndTime = LocalTime.parse(showtime.getEndTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));

                // Kiểm tra nếu newStartTime không nằm trong khoảng thời gian của bất kỳ showtime đã có
                if (!newStartTime.isAfter(scheduledEndTime) && !newEndTime.isBefore(scheduledStartTime)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Phương thức tính toán endTime dựa trên movieId và startTime.
     *
     * @param movieId   ID của phim để tính endTime
     * @param startTime Thời gian bắt đầu để tính endTime
     * @return endTime tính được dưới dạng chuỗi HH:mm:ss
     */
    private String calculateEndTime(int movieId, String startTime) {
        MovieEntity movie = movieRepository.findById(movieId);

        // Giả định rằng duration là số phút
        LocalTime startTimeLocal = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime endTimeLocal = startTimeLocal.plusMinutes(movie.getDuration());

        return endTimeLocal.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public List<RoomEntity> getAllRoomsWithShowTimes() {
        return roomRepository.findAllRoomsNotInAnyShowTime();
    }

    public List<RoomEntity> findRoomsScheduledBefore(String endTime) {
        return showTimeRepository.findRoomsScheduledBefore(endTime);
    }

    public List<RoomEntity> findRoomsByScheduleStartTimeNotBetweenShowTime(String startTime) {
        return showTimeRepository.findRoomsByScheduleStartTimeNotBetweenShowTime(startTime);
    }

    public List<RoomEntity> findShowTimesBetween(String endTime) {
        return showTimeRepository.findShowTimesBetween(endTime);
    }

    public List<RoomEntity> findRoomsScheduledBeforeAndNotBetween(String startTime) {
        return showTimeRepository.findRoomsScheduledBeforeAndNotBetween(startTime);
    }

//    public List<RoomEntity> findRoomsByCombinedConditions(String startTime) {
//        List<RoomEntity> roomsScheduledBeforeAndNotBetween = showTimeRepository.findRoomsScheduledBeforeAndNotBetween(startTime);
//        List<RoomEntity> roomsNotInAnyShowTime = roomRepository.findAllRoomsNotInAnyShowTime();
//
//        // Hợp nhất kết quả từ hai danh sách trên
//        Set<RoomEntity> combinedRooms = new HashSet<>(roomsScheduledBeforeAndNotBetween);
//        combinedRooms.addAll(roomsNotInAnyShowTime);
//
//        return new ArrayList<>(combinedRooms);
//    }

    public List<RoomEntity> findRoomsByCombinedConditions(String startTime, int daytimeId) {
        List<RoomEntity> roomsScheduledBeforeAndNotBetween = showTimeRepository.findRoomsScheduledBeforeAndNotBetween(startTime);
        List<RoomEntity> roomsNotInAnyShowTimeOnDay = roomRepository.findAllRoomsNotInAnyShowTimeOnDay(daytimeId);

        Set<RoomEntity> combinedRooms = new HashSet<>(roomsScheduledBeforeAndNotBetween);
        combinedRooms.addAll(roomsNotInAnyShowTimeOnDay);

        return new ArrayList<>(combinedRooms);
    }

}
