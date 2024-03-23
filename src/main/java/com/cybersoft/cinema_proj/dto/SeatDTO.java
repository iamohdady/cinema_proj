package com.cybersoft.cinema_proj.dto;

import lombok.Data;

@Data
public class SeatDTO {
    private int id;

    private String name;

    private boolean isBooked;

//    private int roomId;
}