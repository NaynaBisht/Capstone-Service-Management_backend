package com.app.technician.dto.response;

import java.util.Set;

import com.app.technician.model.SkillType;

public record AllTechnicians(
	    String technicianId,
	    String userId,
	    String name,
	    Set<SkillType> skills,
	    String city,
	    int experienceYears
	) {}

