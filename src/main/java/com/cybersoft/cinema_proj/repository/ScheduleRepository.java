package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {
    @Query("SELECT s FROM ScheduleEntity s WHERE s.day_time.id = :dayTimeId")
    List<ScheduleEntity> findByDayTimeId(@Param("dayTimeId") int dayTimeId);}
