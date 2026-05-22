package com.example.enrollment.presentation;

import com.example.enrollment.domain.clazz.Class;
import com.example.enrollment.domain.clazz.ClassStatus;
import lombok.*;
import java.time.*;

@Getter
@AllArgsConstructor
public class ClassDetailResponse {

    private Long id;
    private String title;
    private String description;
    private int price;
    private int capacity;
    private int enrolledCount;
    private ClassStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    public static ClassDetailResponse from(Class clazz, int enrolledCount) {
        return new ClassDetailResponse(
                clazz.getId(),
                clazz.getTitle(),
                clazz.getDescription(),
                clazz.getPrice(),
                clazz.getCapacity(),
                enrolledCount,
                clazz.getStatus(),
                clazz.getStartDate(),
                clazz.getEndDate(),
                clazz.getCreatedAt()
        );
    }
}
