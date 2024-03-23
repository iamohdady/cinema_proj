package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "seat_booking")
public class SeatBookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private ShowTimeEntity showtime_id;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private SeatEntity seat_id;

    private boolean isBooked;
}