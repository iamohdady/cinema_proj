package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "seat")
@Entity
@NoArgsConstructor
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private boolean isBooked;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;
}
