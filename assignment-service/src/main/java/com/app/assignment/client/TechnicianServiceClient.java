package com.app.assignment.client;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TechnicianServiceClient {

	private final RestClient restClient;

	@Value("${technician.service.url}")
	private String technicianServiceUrl;

	public TechnicianServiceClient() {
		this.restClient = RestClient.create();
	}

	public TechnicianDTO findAvailableTechnician(String serviceId) {
		return restClient.get()
				.uri(technicianServiceUrl + "/internal/technicians/available?serviceId={serviceId}", serviceId)
				.retrieve().body(TechnicianDTO.class);
	}

	public TechnicianDTO getTechnicianById(String technicianId) {
		return restClient.get()
				// Make sure your Technician Service has this endpoint.
				// If not, change this path to match your Controller (e.g. "/api/technicians/" +
				// technicianId)
				.uri(technicianServiceUrl + "/internal/technicians/" + technicianId).retrieve()
				.body(TechnicianDTO.class);
	}

	@Data
	public static class TechnicianDTO {
		private String technicianId;
		private String userId;
		private String email;
	}
}