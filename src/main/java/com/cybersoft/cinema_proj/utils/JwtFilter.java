package com.cybersoft.cinema_proj.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String headerValue = request.getHeader("Authorization");

        if (headerValue != null && headerValue.startsWith("Bearer ")) {
            String token = headerValue.substring(7);
            String data = jwtHelper.parserToken(token);
            System.out.println("Kiemtra: " + data);

            if (data != null && !data.isEmpty()) {
                String[] tokenData = data.split(",");
                String username = tokenData[0];
                String role = tokenData[1];

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } else {
            System.out.println("Nội dung header không hợp lệ.");
        }
        filterChain.doFilter(request, response);
    }
}

