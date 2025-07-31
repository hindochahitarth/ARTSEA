package com.art.artsea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.art.artsea")
@EntityScan(basePackages = "com.art.artsea")
public class ArtseaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtseaApplication.class, args);
	}


}
