package com.example.demo;

import com.skilldev.certification.CertificationServiceApplication;
import com.skilldev.certification.client.ValidationServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = CertificationServiceApplication.class)
@ActiveProfiles("test")
class CertificationServiceApplicationTests {

	@MockBean
	ValidationServiceClient validationServiceClient;

	@Test
	void contextLoads() {
	}
}
