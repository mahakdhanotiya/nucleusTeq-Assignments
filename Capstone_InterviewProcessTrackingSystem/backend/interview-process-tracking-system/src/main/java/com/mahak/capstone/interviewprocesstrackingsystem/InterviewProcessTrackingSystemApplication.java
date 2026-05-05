package com.mahak.capstone.interviewprocesstrackingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableAsync
public class InterviewProcessTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewProcessTrackingSystemApplication.class, args);
	}

}
