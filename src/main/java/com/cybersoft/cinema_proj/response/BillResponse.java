package com.cybersoft.cinema_proj.response;

import com.cybersoft.cinema_proj.entity.TicketEntity;
import lombok.Data;

import java.util.List;

@Data
public class BillResponse {

    private List<TicketEntity> tickets;

}
