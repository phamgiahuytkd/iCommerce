package com.example.iCommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ICommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ICommerceApplication.class, args);
	}

}
