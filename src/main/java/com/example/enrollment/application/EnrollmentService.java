package com.example.enrollment.application;

import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.common.exception.ErrorCode;
import com.example.enrollment.domain.clazz.ClassRepository;
import com.example.enrollment.domain.clazz.Class;
import com.example.enrollment.domain.clazz.ClassStatus;
import com.example.enrollment.domain.enrollment.Enrollment;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import com.example.enrollment.presentation.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ClassRepository classRepository;

    @Transactional
    public EnrollmentResponse enroll(Long classId, Long userId) {
        // 비관적 락으로 Class 조회
        Class clazz = classRepository.findByIdWithLock(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND));

        // 강의 상태 체크
        if (clazz.getStatus() != ClassStatus.OPEN) {
            throw new BusinessException(ErrorCode.CLASS_NOT_OPEN);
        }

        // 중복 신청 체크
        if (enrollmentRepository.existsByClassIdAndUserId(classId, userId)) {
            throw new BusinessException(ErrorCode.ENROLLMENT_DUPLICATE);
        }

        // 정원 체크
        int activeCount = enrollmentRepository.countActiveEnrollments(classId);
        if (activeCount >= clazz.getCapacity()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_CAPACITY_EXCEEDED);
        }

        Enrollment enrollment = Enrollment.create(classId, userId);
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentResponse confirm(Long enrollmentId, Long userId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
        enrollment.confirm();
        return EnrollmentResponse.from(enrollment);
    }

    @Transactional
    public EnrollmentResponse cancel(Long enrollmentId, Long userId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
        enrollment.cancel();
        return EnrollmentResponse.from(enrollment);
    }

    public List<EnrollmentResponse> findMyEnrollments(Long userId) {
        return enrollmentRepository.findAllByUserId(userId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }
}
