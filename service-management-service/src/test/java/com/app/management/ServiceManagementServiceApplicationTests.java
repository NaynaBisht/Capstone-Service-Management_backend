package com.app.management;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	    "JWT_SECRET=temporary_secret_key_for_testing_123",
	    "JWT_EXPIRATION=3600000"
	})
class ServiceManagementServiceApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
    void mainMethodRunsWithoutException() {
		ServiceManagementServiceApplication.main(new String[] {});
    }
}
