package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.dto.MovieDTO;
import com.cybersoft.cinema_proj.dto.RoomDTO;
import com.cybersoft.cinema_proj.dto.ScheduleDTO;
import com.cybersoft.cinema_proj.dto.ShowTimeDTO;
import com.cybersoft.cinema_proj.entity.*;
import lombok.Data;

import java.util.List;

@Data

public class DaytimeResponse {

    private RoomDTO room;
    private MovieDTO movie;
    private ScheduleDTO schedule;
    private ShowTimeDTO showTime;

}
