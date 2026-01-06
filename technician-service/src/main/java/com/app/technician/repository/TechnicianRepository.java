package com.app.technician.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.app.technician.model.AvailabilityStatus;
import com.app.technician.model.SkillType;
import com.app.technician.model.Technician;
import com.app.technician.model.TechnicianStatus;

public interface TechnicianRepository extends MongoRepository<Technician, String> {

    Optional<Technician> findByEmail(String email);

    List<Technician> findByStatus(TechnicianStatus status);

    Optional<Technician> findByUserId(String userId);

    List<Technician> findByCityAndStatusAndAvailability(
            String city,
            TechnicianStatus status,
            AvailabilityStatus availability
    );
    
    Optional<Technician> findFirstByStatusAndAvailabilityAndSkillsContaining(
            TechnicianStatus status,
            AvailabilityStatus availability,
            SkillType skill
    );
    
    List<Technician> findByStatusAndAvailability(
    	    TechnicianStatus status,
    	    AvailabilityStatus availability
    	);


}

