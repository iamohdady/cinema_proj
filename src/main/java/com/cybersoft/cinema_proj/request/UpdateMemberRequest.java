package com.cybersoft.cinema_proj.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class UpdateMemberRequest {

    private String username;

    private String password;

    private String fullname;

    private String address;

    private String phone;

    private String birthday;

    private String email;

    private String role;

    private MultipartFile image;

    private String number_code;

}
