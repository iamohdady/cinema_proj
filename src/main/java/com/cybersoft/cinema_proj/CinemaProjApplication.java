package com.cybersoft.cinema_proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CinemaProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinemaProjApplication.class, args);
	}

}
