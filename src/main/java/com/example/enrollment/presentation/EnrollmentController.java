package com.example.enrollment.presentation;

import com.example.enrollment.application.EnrollmentService;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/classes/{classId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable Long classId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enroll(classId, userId));
    }

    @PatchMapping("/enrollments/{id}/confirm")
    public ResponseEntity<EnrollmentResponse> confirm(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(enrollmentService.confirm(id, userId));
    }

    @PatchMapping("/enrollments/{id}/cancel")
    public ResponseEntity<EnrollmentResponse> cancel(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(enrollmentService.cancel(id, userId));
    }

    @GetMapping("/enrollments/me")
    public ResponseEntity<List<EnrollmentResponse>> findMyEnrollments(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(enrollmentService.findMyEnrollments(userId));
    }

    @GetMapping("/classes/{classId}/enrollments")
    public ResponseEntity<List<EnrollmentDetailResponse>> getEnrollments(
            @PathVariable Long classId,
            @RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByClass(classId,userId));
    }
}
