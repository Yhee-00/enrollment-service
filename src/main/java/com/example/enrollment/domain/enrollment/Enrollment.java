package com.example.enrollment.domain.enrollment;

import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "user_id"})
) //UNIQUE(user_id, class_id)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long classId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    private LocalDateTime confirmedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Enrollment create(Long classId, Long userId) {
        Enrollment enrollment = new Enrollment();
        enrollment.classId = classId;
        enrollment.userId = userId;
        enrollment.status = EnrollmentStatus.PENDING;
        return enrollment;
    }

    public void confirm() {
        if (this.status != EnrollmentStatus.PENDING) {
            throw new BusinessException(ErrorCode.ENROLLMENT_INVALID_STATUS);
        }
        this.status = EnrollmentStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == EnrollmentStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_CANCELLED);
        }
        if (this.status == EnrollmentStatus.CONFIRMED) {
            if (confirmedAt.plusDays(7).isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.ENROLLMENT_CANCEL_PERIOD_EXPIRED);
            }
        }
        this.status = EnrollmentStatus.CANCELLED;
    }
}
