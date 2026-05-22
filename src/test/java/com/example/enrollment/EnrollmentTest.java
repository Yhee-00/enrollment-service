package com.example.enrollment;

import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.domain.enrollment.Enrollment;
import com.example.enrollment.domain.enrollment.EnrollmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnrollmentTest {

    @Test
    void 수강신청_생성_시_초기_상태는_PENDING이다() {
        Enrollment enrollment = Enrollment.create(1L, 1L);

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    void PENDING에서_CONFIRMED로_결제_확정_성공() {
        Enrollment enrollment = Enrollment.create(1L, 1L);

        enrollment.confirm();

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CONFIRMED);
        assertThat(enrollment.getConfirmedAt()).isNotNull();
    }

    @Test
    void CONFIRMED가_아닌_상태에서_confirm_시_예외() {
        Enrollment enrollment = Enrollment.create(1L, 1L);
        enrollment.cancel();

        assertThatThrownBy(() -> enrollment.confirm())
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void PENDING_상태에서_취소_성공() {
        Enrollment enrollment = Enrollment.create(1L, 1L);

        enrollment.cancel();

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void CONFIRMED_후_7일_이내_취소_성공() {
        Enrollment enrollment = Enrollment.create(1L, 1L);
        enrollment.confirm();

        enrollment.cancel();

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    void CONFIRMED_후_7일_초과_취소_시_예외() {
        Enrollment enrollment = Enrollment.create(1L, 1L);
        enrollment.confirm();
        // confirmedAt을 8일 전으로 조작
        ReflectionTestUtils.setField(enrollment, "confirmedAt", LocalDateTime.now().minusDays(8));

        assertThatThrownBy(() -> enrollment.cancel())
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("취소 가능 기간");
    }

    @Test
    void 이미_취소된_신청_재취소_시_예외() {
        Enrollment enrollment = Enrollment.create(1L, 1L);
        enrollment.cancel();

        assertThatThrownBy(() -> enrollment.cancel())
                .isInstanceOf(BusinessException.class);
    }
}
