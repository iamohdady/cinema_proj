package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Table(name = "member")
@Entity
@NoArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;

    private String image;

    private String fullname;

    private String address;

    private String phone;

    private String birthday;

    private String email;

    private String number_code;

    private String role;

    private double money;

    private boolean status_payment;

    private double amount;
}
