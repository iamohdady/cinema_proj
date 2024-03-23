package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import com.cybersoft.cinema_proj.entity.ScheduleEntity;
import lombok.Data;

@Data
public class DayScheduleReponse {

    private ScheduleEntity schedule;
    private DayTimeEntity daytime;
}
