package com.example.enrollment.application;

import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.common.exception.ErrorCode;
import com.example.enrollment.domain.clazz.Class;
import com.example.enrollment.domain.clazz.ClassRepository;
import com.example.enrollment.domain.clazz.ClassStatus;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import com.example.enrollment.presentation.ClassCreateRequest;
import com.example.enrollment.presentation.ClassDetailResponse;
import com.example.enrollment.presentation.ClassResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassService {

    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public ClassResponse create(ClassCreateRequest request, Long creatorId) {
        Class clazz = Class.create(
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getCapacity(),
                request.getStartDate(),
                request.getEndDate(),
                creatorId
        );
        return ClassResponse.from(classRepository.save(clazz));
    }

    public List<ClassResponse> findAll(ClassStatus status) {
        List<Class> classes = status != null
                ? classRepository.findAllByStatus(status)
                : classRepository.findAll();
        return classes.stream()
                .map(ClassResponse::from)
                .toList();
    }

    public ClassDetailResponse findById(Long id) {
        Class clazz = classRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND));
        int enrolledCount = enrollmentRepository.countActiveEnrollments(id);
        return ClassDetailResponse.from(clazz, enrolledCount);
    }

    @Transactional
    public ClassResponse changeStatus(Long id, ClassStatus newStatus, Long userId) {
        Class clazz = classRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND));
        clazz.changeStatus(newStatus);
        return ClassResponse.from(clazz);
    }
}
