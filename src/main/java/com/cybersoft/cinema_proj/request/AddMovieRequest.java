package com.cybersoft.cinema_proj.request;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class AddMovieRequest {

    private String name;
    private MultipartFile image;
    private MultipartFile trailer;
    private Date start_date;
    private int duration;
    private String rated;
    private String category;
    private String languages;
    private String director;
    private String actor;
    private double price;

}
