package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Table(name = "bill")
@Entity
@NoArgsConstructor
public class BillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date paymentTime;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private MemberEntity member_id;

    private double total_amount;

    private boolean status;
}
