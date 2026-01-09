package com.app.technician.dto.response;

import java.util.Set;

public record AllTechnicians(
	    String technicianId,
	    String userId,
	    String name,
	    Set<String> categoryIds,
	    String city,
	    int experienceYears
	) {}

