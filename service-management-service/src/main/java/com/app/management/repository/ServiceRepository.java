package com.app.management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.app.management.model.ServiceEntity;

public interface ServiceRepository extends MongoRepository<ServiceEntity, String> {

}
