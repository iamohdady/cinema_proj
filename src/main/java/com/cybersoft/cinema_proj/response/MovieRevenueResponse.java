package com.cybersoft.cinema_proj.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieRevenueResponse {

    private Long movieId;
    private String movieName;
    private Double totalRevenue;
}
