package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "news")
@Entity
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String image;

    private String description;

    private String short_desc;
}
