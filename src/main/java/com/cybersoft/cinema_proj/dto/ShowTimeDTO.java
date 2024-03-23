package com.cybersoft.cinema_proj.dto;

import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import com.cybersoft.cinema_proj.entity.MovieEntity;
import com.cybersoft.cinema_proj.entity.RoomEntity;
import com.cybersoft.cinema_proj.entity.ScheduleEntity;
import lombok.Data;

import java.sql.Time;

@Data
public class ShowTimeDTO {
    private int id;

    private String description;

    private MovieEntity movie;

    private RoomEntity room;

    private ScheduleEntity schedule;

    private String endTime;

    private boolean status;
}