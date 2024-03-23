package com.cybersoft.cinema_proj.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MemberDTO {
    private int id;

    private String username;

    private String password;

    private String fullname;

    private String address;

    private String phone;

    private String birthday;

    private String email;

    private String role;

    private String image;

    private String number_code;

    private int point;

    private String rate;

    private double money;

    private boolean status_payment;

    private double amount;
}
