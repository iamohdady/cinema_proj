package com.cybersoft.cinema_proj.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class UpdateMovieRequest {

    private String name;
    private MultipartFile image;
    private MultipartFile trailer;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date start_date;
    private int duration;
    private String category;
    private String director;
    private String actor;
    private double price;
}
