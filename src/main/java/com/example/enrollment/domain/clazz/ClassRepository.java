package com.example.enrollment.domain.clazz;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class,Long> {
    List<Class> findAllByStatus(ClassStatus status);

    // 수강 신청
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Class c WHERE c.id = :id")
    Optional<Class> findByIdWithLock(@Param("id") Long id);
}
