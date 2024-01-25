package org.example.novicesranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NovicesrankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovicesrankingApplication.class, args);
	}

}
