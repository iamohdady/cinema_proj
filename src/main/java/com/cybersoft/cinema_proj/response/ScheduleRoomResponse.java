package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.entity.RoomEntity;
import com.cybersoft.cinema_proj.entity.ScheduleEntity;
import lombok.Data;

@Data
public class ScheduleRoomResponse {

    private ScheduleEntity schedule;
    private RoomEntity room;
}
