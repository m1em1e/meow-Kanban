package com.godotvillage.meowkanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MeowKanbanApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeowKanbanApplication.class, args);
	}

}
