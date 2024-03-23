package com.cybersoft.cinema_proj.dto;
import lombok.Data;

import java.util.Date;

@Data
public class MovieDTO {

    private int id;

    private String name;

    private String image;

    private Date start_date;

    private int duration;

    private String rated;

    private String trailer;

    private String category;

    private String director;

    private String actor;

    private double price;

    private double money;
}