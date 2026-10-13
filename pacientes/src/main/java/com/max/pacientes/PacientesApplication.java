package com.max.pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.max.pacientes", "com.max.commons"})
public class PacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesApplication.class, args);
	}

}
