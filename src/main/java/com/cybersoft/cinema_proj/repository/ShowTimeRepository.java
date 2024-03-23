package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.ShowTimeEntity;
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

}
