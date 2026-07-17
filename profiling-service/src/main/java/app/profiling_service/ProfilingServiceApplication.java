package app.profiling_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProfilingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfilingServiceApplication.class, args);
	}

}
