package com.cybersoft.cinema_proj.response;

import lombok.Data;

@Data
public class ShowtimeDetailsDTO {
    private int roomId;
    private String startTime;
    private String endTime;

    public ShowtimeDetailsDTO(int roomId, String startTime, String endTime) {
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}