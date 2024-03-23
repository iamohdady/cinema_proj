package com.cybersoft.cinema_proj.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtHelper {

    @Value("${custom.token.key}")
    private String secKey;

    private long expiredTime = 8 * 60 * 60 * 1000;

    public String generateToken(String username, String role){
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secKey));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredTime);

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact();
        return token;
    }

    public String parserToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secKey));
        Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);

        String username = claimsJws.getBody().getSubject();
        String role = (String) claimsJws.getBody().get("role");

        return "Username: " + username + ", Role: " + role;
    }
}