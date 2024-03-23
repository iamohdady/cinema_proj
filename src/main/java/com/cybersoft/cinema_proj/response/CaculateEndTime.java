package com.cybersoft.cinema_proj.response;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class CaculateEndTime {

    private int movieId;
    private String startTime;
}
