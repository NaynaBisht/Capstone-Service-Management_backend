package com.app.assignment.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class TechnicianServiceClientTest {

    private TechnicianServiceClient technicianServiceClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        technicianServiceClient = new TechnicianServiceClient();

        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        try {
            var urlField = TechnicianServiceClient.class.getDeclaredField("technicianServiceUrl");
            urlField.setAccessible(true);
            urlField.set(technicianServiceClient, "http://localhost:8082");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            var clientField = TechnicianServiceClient.class.getDeclaredField("restClient");
            clientField.setAccessible(true);
            clientField.set(technicianServiceClient, restClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAvailableTechnician_shouldReturnTechnician() {
        String responseJson = """
                {
                  "technicianId": "T123",
                  "userId": "U001",
                  "email": "tech@example.com"
                }
                """;

        mockServer.expect(requestTo("http://localhost:8082/internal/technicians/available?serviceId=SVC01"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        TechnicianServiceClient.TechnicianDTO tech =
                technicianServiceClient.findAvailableTechnician("SVC01");

        mockServer.verify();

        assertEquals("T123", tech.getTechnicianId());
        assertEquals("U001", tech.getUserId());
        assertEquals("tech@example.com", tech.getEmail());
    }

    @Test
    void getTechnicianById_shouldReturnTechnician() {
        String responseJson = """
                {
                  "technicianId": "T999",
                  "userId": "U999",
                  "email": "tech2@example.com"
                }
                """;

        mockServer.expect(requestTo("http://localhost:8082/internal/technicians/T999"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        TechnicianServiceClient.TechnicianDTO tech =
                technicianServiceClient.getTechnicianById("T999");

        mockServer.verify();

        assertEquals("T999", tech.getTechnicianId());
        assertEquals("U999", tech.getUserId());
        assertEquals("tech2@example.com", tech.getEmail());
    }
}
