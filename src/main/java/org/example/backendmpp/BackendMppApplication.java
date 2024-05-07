package org.example.backendmpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableScheduling
public class BackendMppApplication {

	public static void main(String[] args) {
		run(BackendMppApplication.class, args);
	}

}
