package com.raven.training;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TrainingApplicationTests {

	private ApplicationContext applicationContext;

	public TrainingApplicationTests(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	@DisplayName("Should load the Spring Boot application context successfully")
	void contextLoads() {
		assertNotNull(applicationContext, "The Spring Boot application context should not be null");
	}

	@Test
	@DisplayName("Should run the main method without throwing exceptions")
	void mainMethodRunsSuccessfully() {

		TrainingApplication.main(new String[]{});
	}
}
