package com.app.assignment.controller;

import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class TechnicianAssignmentController {

	private final AssignmentService assignmentService;

	@GetMapping("/my")
	public List<AssignmentResponse> getMyAssignments(Authentication authentication) {

		// userId extracted from JwtAuthenticationFilter
		String technicianUserId = authentication.getName();

		return assignmentService.getMyAssignments(technicianUserId);
	}

	@PutMapping("/{assignmentId}/accept")
	public AssignmentResponse acceptAssignment(@PathVariable String assignmentId, Authentication authentication) {
		String technicianUserId = authentication.getName();

		return assignmentService.acceptAssignment(assignmentId, technicianUserId);
	}

	@PutMapping("/{assignmentId}/reject")
	public AssignmentResponse rejectAssignment(@PathVariable String assignmentId, Authentication authentication) {
		String technicianUserId = authentication.getName();

		return assignmentService.rejectAssignment(assignmentId, technicianUserId);
	}

	@PutMapping("/{assignmentId}/start")
	public AssignmentResponse startAssignment(
	        @PathVariable String assignmentId,
	        Authentication authentication
	) {
	    String technicianUserId = authentication.getName();

	    return assignmentService.startAssignment(
	            assignmentId,
	            technicianUserId
	    );
	}
	 @PutMapping("/{assignmentId}/complete")
	    public AssignmentResponse completeAssignment(
	            @PathVariable String assignmentId,
		        Authentication authentication
	    ) {
		 	String technicianUserId = authentication.getName();

	        return assignmentService.completeAssignment(
		            assignmentId,
		            technicianUserId
		    );
	    }

}
