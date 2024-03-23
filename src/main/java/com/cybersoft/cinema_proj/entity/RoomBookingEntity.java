package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "room_booking")
public class RoomBookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private ShowTimeEntity showtime_id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room_id;

    private boolean isBooked;
}
