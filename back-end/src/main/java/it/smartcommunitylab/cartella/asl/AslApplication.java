package it.smartcommunitylab.cartella.asl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AslApplication {

	public static void main(String[] args) {
		SpringApplication.run(AslApplication.class, args);
	}
}
