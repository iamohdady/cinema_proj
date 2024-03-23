package com.cybersoft.cinema_proj.dto;

import com.cybersoft.cinema_proj.entity.MemberEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BillDTO {
    private int id;
    private Date paymentTime;
    private MemberEntity member_id;
    private double total_amount;
    private List<TicketDTO> tickets;
    private boolean status;
}