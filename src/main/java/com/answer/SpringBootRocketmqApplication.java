package com.answer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.answer.boot")
public class SpringBootRocketmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRocketmqApplication.class, args);
	}

}
