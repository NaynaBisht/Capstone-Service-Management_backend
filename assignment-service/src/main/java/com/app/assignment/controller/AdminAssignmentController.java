package com.app.assignment.controller;

import com.app.assignment.dto.request.CreateAssignmentRequest;
import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AdminAssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse createAssignment(
            @RequestBody CreateAssignmentRequest request
    ) {
        return assignmentService.createAssignment(request);
    }
}
