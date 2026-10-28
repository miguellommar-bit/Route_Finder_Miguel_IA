package com.miguel.routefinder;

import com.miguel.routefinder.model.Airport;
import com.miguel.routefinder.repositories.AirportRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoutefinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutefinderApplication.class, args);
	}
}