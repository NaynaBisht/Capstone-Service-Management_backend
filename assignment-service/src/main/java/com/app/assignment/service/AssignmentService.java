package com.app.assignment.service;

import com.app.assignment.dto.response.AssignmentResponse;
import com.app.assignment.model.Assignment;
import com.app.assignment.model.AssignmentStatus;
import com.app.assignment.repository.AssignmentRepository;
import com.app.assignment.util.AssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

	private final AssignmentRepository assignmentRepository;

	public List<AssignmentResponse> getAssignmentsForTechnician(String technicianId, AssignmentStatus status) {

		List<Assignment> assignments;

		if (status != null) {
			assignments = assignmentRepository.findByTechnicianIdAndStatusIn(technicianId, List.of(status));
		} else {
			assignments = assignmentRepository.findByTechnicianId(technicianId);
		}

		return assignments.stream().map(AssignmentMapper::toResponse).collect(Collectors.toList());
	}
}
