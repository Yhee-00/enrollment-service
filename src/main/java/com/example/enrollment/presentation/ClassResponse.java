package com.example.enrollment.presentation;

import com.example.enrollment.domain.clazz.Class;
import com.example.enrollment.domain.clazz.ClassStatus;
import lombok.*;
import java.time.*;

@Getter
@AllArgsConstructor
public class ClassResponse {

    private Long id;
    private String title;
    private int price;
    private int capacity;
    private ClassStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    public static ClassResponse from(Class clazz) {
        return new ClassResponse(
                clazz.getId(),
                clazz.getTitle(),
                clazz.getPrice(),
                clazz.getCapacity(),
                clazz.getStatus(),
                clazz.getStartDate(),
                clazz.getEndDate(),
                clazz.getCreatedAt()
        );
    }
}
