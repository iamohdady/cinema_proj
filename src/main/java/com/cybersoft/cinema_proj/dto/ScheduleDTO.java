package com.cybersoft.cinema_proj.dto;

import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import lombok.Data;

@Data
public class ScheduleDTO {
    private int id;

    private String startTime;

    private String description;

    private DayTimeEntity day_time;
}