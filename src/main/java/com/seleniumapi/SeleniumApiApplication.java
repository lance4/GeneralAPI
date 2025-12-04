package com.seleniumapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(info = @Info(title = "Stock migration", version = "1.0"))
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SeleniumApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeleniumApiApplication.class, args);
	}
}
