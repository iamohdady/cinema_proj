package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Table(name = "showtime")
@Entity
@NoArgsConstructor
public class ShowTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "movies_id")
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    private String endTime;

    private boolean status;
}
