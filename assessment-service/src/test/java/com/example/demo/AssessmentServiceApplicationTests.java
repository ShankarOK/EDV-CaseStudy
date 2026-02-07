package com.example.demo;

import com.skilldev.assessment.AssessmentServiceApplication;
import com.skilldev.assessment.client.CertificationServiceClient;
import com.skilldev.assessment.client.ValidationServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = AssessmentServiceApplication.class)
@ActiveProfiles("test")
class AssessmentServiceApplicationTests {

	@MockBean
	ValidationServiceClient validationServiceClient;
	@MockBean
	CertificationServiceClient certificationServiceClient;

	@Test
	void contextLoads() {
	}
}
