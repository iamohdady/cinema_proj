package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayTimeRepository extends JpaRepository<DayTimeEntity, Integer> {

    @Query("SELECT r, m, s, st " +
        "FROM DayTimeEntity d " +
        "LEFT JOIN ScheduleEntity s ON d.id = s.day_time.id " +
        "LEFT JOIN ShowTimeEntity st ON s.id = st.schedule.id " +
        "LEFT JOIN MovieEntity m ON st.movie.id = m.id " +
        "LEFT JOIN RoomEntity r ON st.room.id = r.id " +
        "WHERE d.id = :daytimeId")
    List<Object[]> findRoomMovieScheduleShowTimeByDaytimeId(int daytimeId);
}
