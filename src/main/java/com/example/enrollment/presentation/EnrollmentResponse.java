package com.example.enrollment.presentation;

import com.example.enrollment.domain.enrollment.Enrollment;
import com.example.enrollment.domain.enrollment.EnrollmentStatus;
import lombok.*;
import java.time.*;

@Getter
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    private Long classId;
    private Long userId;
    private EnrollmentStatus status;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;

    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getClassId(),
                enrollment.getUserId(),
                enrollment.getStatus(),
                enrollment.getConfirmedAt(),
                enrollment.getCreatedAt()
        );
    }
}