package com.cybersoft.cinema_proj.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NewsRequest {

    private String name;

    private MultipartFile image;

    private String description;

    private String short_desc;
}
