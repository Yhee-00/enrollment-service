package com.example.enrollment.domain.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findAllByUserId(Long userId);

    List<Enrollment> findAllByClassId(Long classId);

    boolean existsByClassIdAndUserId(Long classId, Long userId);

    //정원 체크
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.classId = :classId AND e.status IN ('PENDING', 'CONFIRMED') ")
    int countActiveEnrollments(@Param("classId") Long classId);
}
