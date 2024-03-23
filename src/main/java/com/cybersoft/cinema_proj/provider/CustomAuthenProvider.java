package com.cybersoft.cinema_proj.provider;

import com.cybersoft.cinema_proj.entity.MemberEntity;
import com.cybersoft.cinema_proj.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenProvider implements AuthenticationProvider {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        MemberEntity member = memberRepository.findByUsername(username);
        if (member != null) {
            if (passwordEncoder.matches(password, member.getPassword())) {

                List<GrantedAuthority> roles = new ArrayList<>();
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole());
                roles.add(grantedAuthority);

                // Tạo chứng thực
                UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, member.getPassword(), roles);

                SecurityContextHolder.getContext().setAuthentication(token);

                return token;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}