package com.app.assignment.controller;

import com.app.assignment.dto.request.CancelAssignmentRequest;
import com.app.assignment.dto.request.CreateAssignmentRequest;
import com.app.assignment.dto.request.ReassignAssignmentRequest;
import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.service.AssignmentService;

import jakarta.validation.Valid;
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
	public AssignmentResponse createAssignment(@Valid @RequestBody CreateAssignmentRequest request) {
		return assignmentService.createAssignment(request);
	}

	@PutMapping("/{assignmentId}/cancel")
	public AssignmentResponse cancelAssignment(@PathVariable String assignmentId,
			@RequestBody CancelAssignmentRequest request) {
		return assignmentService.cancelAssignment(assignmentId, request.getReason());
	}

	@PutMapping("/{assignmentId}/reassign")
	public AssignmentResponse reassignAssignment(@PathVariable String assignmentId,
			@RequestBody ReassignAssignmentRequest request) {
		return assignmentService.reassignAssignment(assignmentId, request);
	}

}
