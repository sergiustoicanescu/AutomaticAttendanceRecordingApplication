package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class AutomaticAttendanceBackend {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Bucharest"));

		SpringApplication.run(AutomaticAttendanceBackend.class, args);
	}
}
