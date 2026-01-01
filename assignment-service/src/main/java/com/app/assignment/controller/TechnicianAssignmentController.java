package com.app.assignment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.model.AssignmentStatus;
import com.app.assignment.service.AssignmentService;
import com.app.assignment.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class TechnicianAssignmentController {

    private final AssignmentService assignmentService;
    private final JwtUtil jwtUtil;

    @GetMapping("/my")
    public List<AssignmentResponse> getMyAssignments(
            @RequestParam(required = false) AssignmentStatus status
    ) {

        // Extract technicianId from JWT
        String technicianId = jwtUtil.getLoggedInUserId();

        return assignmentService.getAssignmentsForTechnician(technicianId, status);
    }
}
