package com.app.technician.dto.request;

import com.app.technician.model.AvailabilityStatus;

import lombok.Data;

@Data
public class UpdateAvailabilityRequest {
    private AvailabilityStatus availability;
}
