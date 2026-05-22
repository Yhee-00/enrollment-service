package com.example.enrollment.presentation;

import com.example.enrollment.domain.enrollment.Enrollment;
import com.example.enrollment.domain.enrollment.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrollmentDetailResponse {
    private Long id;
    private Long userId;
    private EnrollmentStatus status;


    public static EnrollmentDetailResponse from(Enrollment enrollment) {
        return new EnrollmentDetailResponse(
                enrollment.getId(),
                enrollment.getUserId(),
                enrollment.getStatus()
        );
    }
}
