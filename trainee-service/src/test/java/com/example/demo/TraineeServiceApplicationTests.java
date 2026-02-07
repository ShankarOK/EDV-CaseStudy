package com.example.demo;

import com.skilldev.trainee.TraineeServiceApplication;
import com.skilldev.trainee.client.ValidationServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TraineeServiceApplication.class)
@ActiveProfiles("test")
class TraineeServiceApplicationTests {

	@MockBean
	ValidationServiceClient validationServiceClient;

	@Test
	void contextLoads() {
	}

}
