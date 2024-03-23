package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "schedule")
@Entity
@NoArgsConstructor
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String startTime;

    private String description;

    @ManyToOne
    @JoinColumn(name = "daytime_id")
    private DayTimeEntity day_time;
}
