package com.assurance.assuranceback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = {
		"com.assurance.assuranceback.Entity",
		"com.assurance.assuranceback.Entity.UserEntity",
		"com.assurance.assuranceback.Entity.LoyaltyEntity"
})

@EnableJpaRepositories(basePackages = {"com.assurance.assuranceback.Repository"})
public class AssuranceBackApplication {
	public static void main(String[] args) {
		SpringApplication.run(AssuranceBackApplication.class, args);
	}
}
