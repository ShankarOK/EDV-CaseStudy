package com.skilldev.trainee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TraineeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraineeServiceApplication.class, args);
	}

}
