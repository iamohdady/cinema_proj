package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.MemberDTO;
import com.cybersoft.cinema_proj.request.LoginRequest;
import com.cybersoft.cinema_proj.request.RegisterRequest;
import com.cybersoft.cinema_proj.response.TokenResponse;
import com.cybersoft.cinema_proj.service.MemberService;
import com.cybersoft.cinema_proj.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest request){

        UsernamePasswordAuthenticationToken authen = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authen);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<SimpleGrantedAuthority> roles = (List<SimpleGrantedAuthority>) authentication.getAuthorities();

        String role = roles.get(0).getAuthority();

        String token = jwtHelper.generateToken(request.getUsername(), role);
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(token);

        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute RegisterRequest request) {
        if (request.getImage() == null) {
            return ResponseEntity.badRequest().body("Image is required");
        }
        MemberDTO memberDTO = memberService.convertToRegisterRequestToDTO(request);
        MemberDTO savedMemberDTO = memberService.register(memberDTO);

        if (savedMemberDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Member already exists");
        }
        return ResponseEntity.ok(savedMemberDTO);
    }


}
