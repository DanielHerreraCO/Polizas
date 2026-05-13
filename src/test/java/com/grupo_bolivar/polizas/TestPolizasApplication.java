package com.grupo_bolivar.polizas;

import org.springframework.boot.SpringApplication;

public class TestPolizasApplication {

	public static void main(String[] args) {
		SpringApplication.from(PolizasApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
