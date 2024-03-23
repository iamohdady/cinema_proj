package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

    RoomEntity findById(Long id);

    @Query("SELECT r FROM RoomEntity r WHERE r.id NOT IN " +
        "(SELECT rb.room_id.id FROM RoomBookingEntity rb " +
        "WHERE rb.isBooked <> FALSE AND rb.showtime_id.endTime > :startTime)")
    List<RoomEntity> findAvailableRooms(@Param("startTime") String startTime);


    //    @Query("SELECT DISTINCT r FROM RoomEntity r " +
//        "JOIN r.schedules s " +
//        "WHERE s.day_time.day_time = :daytime " +
//        "AND s.startTime >= :startTime " +
//        "AND s.startTime <= :endTime")
//    List<RoomEntity> findRoomsByDaytimeAndTimeRange(
//        @Param("daytime") String daytime,
//        @Param("startTime") String startTime,
//        @Param("endTime") String endTime
//    );
    @Query("SELECT r FROM RoomEntity r WHERE r.id NOT IN (SELECT s.room.id FROM ShowTimeEntity s WHERE s.schedule.day_time.id = :dayTimeId)")
    List<RoomEntity> findAvailableRoomsByDayTime(@Param("dayTimeId") int dayTimeId);

//    @Query("SELECT s.room FROM ShowTimeEntity s WHERE s.schedule.day_time.id = :dayTimeId AND s.endTime < :startTime")
//    List<RoomEntity> findRoomsByDayTimeAndStartTime(@Param("dayTimeId") int dayTimeId, @Param("startTime") String startTime);

//    @Query("SELECT s.room FROM ShowTimeEntity s " +
//        "WHERE s.schedule.day_time.id = :dayTimeId " +
//        "AND s.endTime < :startTime " +
//        "AND NOT EXISTS (SELECT 1 FROM ShowTimeEntity s2 " +
//        "WHERE s2.schedule.id = s.schedule.id " +
//        "AND :startTime BETWEEN s2.schedule.startTime AND s2.endTime)")
//    List<RoomEntity> findRoomsByDayTimeAndStartTime(
//        @Param("dayTimeId") int dayTimeId,
//        @Param("startTime") String startTime);

    @Query("SELECT DISTINCT r FROM RoomEntity r " +
        "LEFT JOIN ShowTimeEntity s " +
        "ON r.id = s.room.id " +
        "AND s.schedule.day_time.id = :dayTimeId " +
        "AND s.endTime < :startTime " +
        "AND NOT EXISTS (" +
        "    SELECT 1 FROM ShowTimeEntity s2 " +
        "    WHERE s2.schedule.id = s.schedule.id " +
        "    AND :startTime BETWEEN s2.schedule.startTime AND s2.endTime" +
        ") " +
        "WHERE s.id IS NULL")
    List<RoomEntity> findRoomsByDayTimeAndStartTime(
        @Param("dayTimeId") int dayTimeId,
        @Param("startTime") String startTime);

    @Query("SELECT DISTINCT r FROM RoomEntity r WHERE r.id NOT IN (SELECT DISTINCT s.room.id FROM ShowTimeEntity s)")
    List<RoomEntity> findAllRoomsNotInAnyShowTime();

    @Query("SELECT DISTINCT r FROM RoomEntity r " +
        "WHERE r.id NOT IN (SELECT DISTINCT s.room.id FROM ShowTimeEntity s WHERE s.schedule.day_time.id = :daytimeId)")
    List<RoomEntity> findAllRoomsNotInAnyShowTimeOnDay(@Param("daytimeId") int daytimeId);
}

