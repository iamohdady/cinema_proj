package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import com.cybersoft.cinema_proj.entity.ScheduleEntity;
import com.cybersoft.cinema_proj.entity.ShowTimeEntity;
import lombok.Data;

@Data
public class ShowTimeReponse {

    private ShowTimeEntity showtime;
    private ScheduleEntity schedule;
    private DayTimeEntity daytime;
}
