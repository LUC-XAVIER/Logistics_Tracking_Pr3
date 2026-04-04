package com.example.logistics_tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LogisticsTrackingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogisticsTrackingApplication.class, args);
	}

}
