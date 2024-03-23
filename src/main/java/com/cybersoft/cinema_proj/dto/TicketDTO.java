package com.cybersoft.cinema_proj.dto;

import com.cybersoft.cinema_proj.entity.BillEntity;
import com.cybersoft.cinema_proj.entity.SeatBookingEntity;
import com.cybersoft.cinema_proj.entity.SeatEntity;
import com.cybersoft.cinema_proj.entity.ShowTimeEntity;
import lombok.Data;

@Data
public class TicketDTO {

    private int id;

    private double price;

    private String codeQR;

    private String description;

    private ShowTimeEntity showtime_id;

    private BillEntity bill_id;

    private SeatEntity seat_id;

    private SeatBookingEntity seat_booking_id;
}
