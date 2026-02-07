package com.example.demo;

import com.skilldev.course.CourseServiceApplication;
import com.skilldev.course.client.TrainerServiceClient;
import com.skilldev.course.client.ValidationServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = CourseServiceApplication.class)
@ActiveProfiles("test")
class CourseServiceApplicationTests {

	@MockBean
	ValidationServiceClient validationServiceClient;
	@MockBean
	TrainerServiceClient trainerServiceClient;

	@Test
	void contextLoads() {
	}

}
