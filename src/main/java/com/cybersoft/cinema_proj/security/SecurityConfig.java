package com.cybersoft.cinema_proj.security;
import com.cybersoft.cinema_proj.provider.CustomAuthenProvider;
import com.cybersoft.cinema_proj.utils.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    CustomAuthenProvider customAuthenProvider;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(customAuthenProvider)
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/login/**").permitAll()
            .requestMatchers("/member/**").permitAll()
            .requestMatchers("/tickets/**").permitAll()
            .requestMatchers("/rooms/**").permitAll()
            .requestMatchers("/seats/**").permitAll()
            .requestMatchers("/showtimes/**").permitAll()
            .requestMatchers("/bill/**").permitAll()
            .requestMatchers("/movie/**").permitAll()
            .requestMatchers("/daytimes/**").permitAll()
            .requestMatchers("/revenue/**").permitAll()
            .anyRequest().authenticated()
            .and() /*.httpBasic()*/
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            /*.and()*/.build();
    }
}