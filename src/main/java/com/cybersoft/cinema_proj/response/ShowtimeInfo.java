package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.entity.RoomEntity;
import lombok.Data;

@Data
public class ShowtimeInfo {
    private String dayTime;

    private String startTime;

    private String endTime;

    private RoomEntity room;

}
