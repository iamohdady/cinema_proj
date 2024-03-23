package com.cybersoft.cinema_proj.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private int id;

    private String name;

    private int capacity;

    private String description;

    private String rate;

    private boolean status;
}