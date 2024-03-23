package com.cybersoft.cinema_proj.dto;

import com.cybersoft.cinema_proj.entity.SeatEntity;
import com.cybersoft.cinema_proj.entity.ShowTimeEntity;

public class SeatBookingDTO {

    private int id;

    private ShowTimeEntity showtime_id;

    private SeatEntity seat_id;

    private boolean isBooked;
}