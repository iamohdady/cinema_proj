package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.RoomEntity;
import com.cybersoft.cinema_proj.entity.ShowTimeEntity;
import com.cybersoft.cinema_proj.response.ShowtimeInfo;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTimeEntity, Integer> {

    @Query("SELECT s FROM ShowTimeEntity s WHERE s.status = TRUE AND s.movie.id = :movieId")
    List<ShowTimeEntity> findByMovieId(int movieId);

    @Query("SELECT s FROM ShowTimeEntity s WHERE s.room.id = :roomId")
    List<ShowTimeEntity> findByRoomId(int roomId);

    @Query("SELECT s FROM ShowTimeEntity s WHERE s.status = TRUE")
    List<ShowTimeEntity> getListShowtime();

    @Query(
        "SELECT s AS schedule, r AS room " +
            "FROM ShowTimeEntity st " +
            "JOIN ScheduleEntity s ON st.schedule.id = s.id " +
            "JOIN RoomEntity r ON st.room.id = r.id " +
            "WHERE st.id = :showTimeId")
    Tuple findScheduleAndRoomByShowTimeId(@Param("showTimeId") int showTimeId);

    @Query("SELECT s.id AS showtimeId, " +
        "sch.startTime AS startTime, " +
        "s.endTime AS endTime, " +
        "dt.day_time AS showDate " +
        "FROM ShowTimeEntity s " +
        "JOIN ScheduleEntity sch ON s.schedule.id = sch.id " +
        "JOIN DayTimeEntity dt ON sch.day_time.id = dt.id")
    List<Tuple> getShowTimeDetails();

    @Query("SELECT s FROM ShowTimeEntity s WHERE FUNCTION('DATE_FORMAT', s.schedule.day_time.day_time, '%Y-%m-%d') = :currentDate AND s.endTime < :currentTime")
    List<ShowTimeEntity> findExpiredShowTimes(@Param("currentDate") String currentDate, @Param("currentTime") String currentTime);

    @Query("SELECT s.id, s.endTime, sc.startTime, d.day_time " +
        "FROM ShowTimeEntity s " +
        "JOIN ScheduleEntity sc ON s.schedule.id = sc.id " +
        "JOIN DayTimeEntity d ON sc.day_time.id = d.id")
    List<Tuple> getTime();

    @Query("SELECT s.id, sc.startTime, d.day_time, r.name " +
        "FROM ShowTimeEntity s " +
        "JOIN s.schedule sc " +
        "JOIN s.room r " +
        "JOIN sc.day_time d")
    List<Object[]> findAllShowTimeDetails();

    @Query("SELECT DISTINCT s.room FROM ShowTimeEntity s " +
        "WHERE s.endTime <  :startTime AND s.status = TRUE")
    List<RoomEntity> findRoomsScheduledBefore(@Param("startTime") String startTime);

    @Query("SELECT DISTINCT s.room FROM ShowTimeEntity s " +
        "WHERE :startTime NOT BETWEEN s.schedule.startTime AND s.endTime")
    List<RoomEntity> findRoomsByScheduleStartTimeNotBetweenShowTime(@Param("startTime") String startTime);

    @Query("SELECT DISTINCT s.room FROM ShowTimeEntity s WHERE s.schedule.startTime >= :endTime")
    List<RoomEntity> findShowTimesBetween(String endTime);

    @Query("SELECT DISTINCT s.room FROM ShowTimeEntity s " +
        "WHERE s.endTime < :startTime " +
        "AND s.status = TRUE " +
        "AND :startTime NOT BETWEEN s.schedule.startTime AND s.endTime " +
        "AND s.schedule.startTime >= :startTime")
    List<RoomEntity> findRoomsByConditions(
        @Param("startTime") String startTime);

    @Query("SELECT DISTINCT s.room FROM ShowTimeEntity s " +
        "WHERE s.endTime < :startTime " +
        "AND (:startTime NOT BETWEEN s.schedule.startTime AND s.endTime)")
    List<RoomEntity> findRoomsScheduledBeforeAndNotBetween(
        @Param("startTime") String startTime
    );

//    @Query("SELECT DISTINCT r FROM RoomEntity r " +
//        "LEFT JOIN ShowTimeEntity s ON r.id = s.room.id " +
//        "WHERE (s.endTime < :startTime AND s.status = FALSE AND :startTime NOT BETWEEN s.schedule.startTime AND s.endTime) " +
//        "OR r.id NOT IN (SELECT DISTINCT s.room.id FROM ShowTimeEntity s)")
//    List<RoomEntity> findRoomsScheduledBeforeAndNotBetweenAndNotInAnyShowTime(@Param("startTime") String startTime);


    // Truy vấn các phòng không có trong bất kỳ lịch chiếu nào
    @Query("SELECT DISTINCT r FROM RoomEntity r WHERE r.id NOT IN (SELECT DISTINCT s.room.id FROM ShowTimeEntity s)")
    List<RoomEntity> findAllRoomsNotInAnyShowTime();

}
