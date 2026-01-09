package com.app.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	    "JWT_SECRET=temporary_secret_key_for_testing_123",
	    "JWT_EXPIRATION=99999999"
	})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
    void mainMethodRunsWithoutException() {
        ApiGatewayApplication.main(new String[] {});
    }

}
