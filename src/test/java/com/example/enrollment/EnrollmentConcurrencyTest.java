package com.example.enrollment;

import com.example.enrollment.application.EnrollmentService;
import com.example.enrollment.domain.clazz.ClassRepository;
import com.example.enrollment.domain.clazz.Class;
import com.example.enrollment.domain.clazz.ClassStatus;
import com.example.enrollment.domain.enrollment.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EnrollmentConcurrencyTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void 동시에_정원_초과_신청_시_정원만큼만_성공() throws InterruptedException {
        // given
        com.example.enrollment.domain.clazz.Class clazz = Class.create("테스트 강의", "설명", 10000, 10,
                LocalDate.now(), LocalDate.now().plusMonths(1), 1L);
        clazz.changeStatus(ClassStatus.OPEN);
        classRepository.save(clazz);

        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1;
            executorService.submit(() -> {
                try {
                    enrollmentService.enroll(clazz.getId(), userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(20);
    }
}
