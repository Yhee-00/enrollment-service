package com.example.enrollment;

import com.example.enrollment.application.EnrollmentService;
import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.domain.clazz.Class;
import com.example.enrollment.domain.clazz.ClassRepository;
import com.example.enrollment.domain.clazz.ClassStatus;
import com.example.enrollment.presentation.EnrollmentDetailResponse;
import jakarta.transaction.Transactional;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EnrollmentIntegrationTest {
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    ClassRepository classRepository;

    @Test
    void DRAFT_상태_강의_신청_시_예외(){
        Class clazz = Class.create("제목","설명",10000,30,
                LocalDate.now(), LocalDate.now().plusMonths(1),1L);
        //OPEN전환 없이 저장
        classRepository.save(clazz);

        assertThatThrownBy(()->enrollmentService.enroll(clazz.getId(),1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 중복_신청_시_예외(){
        Class clazz = Class.create("제목","설명",10000,30,
                LocalDate.now(), LocalDate.now().plusMonths(1),1L);
        clazz.changeStatus(ClassStatus.OPEN);
        classRepository.save(clazz);

        enrollmentService.enroll(clazz.getId(),1L);

        assertThatThrownBy(()-> enrollmentService.enroll(clazz.getId(), 1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void 크리에이터가_수강생_목록_조회_성공(){
        Class clazz = Class.create("제목", "설명", 10000,30,
                LocalDate.now(), LocalDate.now().plusMonths(1),1L);
        clazz.changeStatus(ClassStatus.OPEN);
        classRepository.save(clazz);

        enrollmentService.enroll(clazz.getId(),42L);

        List<EnrollmentDetailResponse> result =
                enrollmentService.getEnrollmentsByClass(clazz.getId(), 1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(42L);
    }

    @Test
    void 크리에이터가_아닌_유저_수강생_목록_조회_시_예외(){
        Class clazz = Class.create("제목","설명",10000,30,
                LocalDate.now(), LocalDate.now().plusMonths(1),1L);
        clazz.changeStatus(ClassStatus.OPEN);
        classRepository.save(clazz);

        assertThatThrownBy(() -> enrollmentService.getEnrollmentsByClass(clazz.getId(), 42L))
                .isInstanceOf(BusinessException.class);
    }
}
