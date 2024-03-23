package com.cybersoft.cinema_proj.request;

import lombok.Data;

import java.util.List;

@Data
public class CancelSeatsRequest {

    private List<Long> seatIds;
}
