package com.app.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {
	@Bean
	public MongoClient mongoClient() {
		// Explicitly defining the local connection
		return MongoClients.create("mongodb://localhost:27017");
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		// This line FORCES the database name to be "Capstone"
		return new MongoTemplate(mongoClient(), "auth_db");
	}
}