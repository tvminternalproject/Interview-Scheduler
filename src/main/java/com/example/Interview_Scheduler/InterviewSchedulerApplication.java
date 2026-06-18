package com.example.Interview_Scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InterviewSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewSchedulerApplication.class, args);
	}

}
