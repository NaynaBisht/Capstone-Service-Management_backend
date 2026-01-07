package com.app.assignment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AssignmentServiceApplication.class, 
properties = {
	    "JWT_SECRET=temporary_secret_key_for_testing_123",
	    "JWT_EXPIRATION=3600000"
	})
class AssignmentServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
