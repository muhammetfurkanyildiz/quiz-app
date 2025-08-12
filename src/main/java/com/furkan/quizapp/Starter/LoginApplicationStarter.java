package com.furkan.quizapp.Starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = {"com.furkan.quizapp"})
@EntityScan(basePackages = {"com.furkan.quizapp"})
@EnableJpaRepositories(basePackages = {"com.furkan.quizapp"})
@EnableScheduling


@SpringBootApplication
public class LoginApplicationStarter {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplicationStarter.class, args);
	}

}
