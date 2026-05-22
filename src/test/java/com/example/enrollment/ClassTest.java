package com.example.enrollment;

import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.domain.clazz.ClassStatus;
import org.junit.jupiter.api.Test;
import com.example.enrollment.domain.clazz.Class;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClassTest {

    @Test
    void 강의_생성_시_초기_상태는_DRAFT이다() {
        Class clazz = Class.create("제목", "설명", 10000, 30,
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);

        assertThat(clazz.getStatus()).isEqualTo(ClassStatus.DRAFT);
    }

    @Test
    void DRAFT에서_OPEN으로_상태_변경_성공() {
        Class clazz = Class.create("제목", "설명", 10000, 30,
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);

        clazz.changeStatus(ClassStatus.OPEN);

        assertThat(clazz.getStatus()).isEqualTo(ClassStatus.OPEN);
    }

    @Test
    void OPEN에서_CLOSED로_상태_변경_성공() {
        Class clazz = Class.create("제목", "설명", 10000, 30,
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        clazz.changeStatus(ClassStatus.OPEN);

        clazz.changeStatus(ClassStatus.CLOSED);

        assertThat(clazz.getStatus()).isEqualTo(ClassStatus.CLOSED);
    }

    @Test
    void DRAFT에서_CLOSED로_직접_변경_시_예외() {
        Class clazz = Class.create("제목", "설명", 10000, 30,
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);

        assertThatThrownBy(() -> clazz.changeStatus(ClassStatus.CLOSED))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void CLOSED에서_OPEN으로_역방향_변경_시_예외() {
        Class clazz = Class.create("제목", "설명", 10000, 30,
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        clazz.changeStatus(ClassStatus.OPEN);
        clazz.changeStatus(ClassStatus.CLOSED);

        assertThatThrownBy(() -> clazz.changeStatus(ClassStatus.OPEN))
                .isInstanceOf(BusinessException.class);
    }
}
