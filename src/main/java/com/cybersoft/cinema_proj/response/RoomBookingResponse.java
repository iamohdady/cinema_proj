package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.dto.*;
import lombok.Data;

@Data
public class RoomBookingResponse{
    private int roomBookingId;
    private ScheduleDTO schedule;
    private DayTimeDTO daytime;
    private ShowTimeDTO showtime;
    private MovieDTO movie;
    private RoomDTO room;
}