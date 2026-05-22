package com.example.enrollment.domain.clazz;

import com.example.enrollment.common.exception.BusinessException;
import com.example.enrollment.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "classes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassStatus status;

    @Column(nullable = false)
    private Long creatorId;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Class create(String title, String description, int price,
                               int capacity, LocalDate startDate, LocalDate endDate,
                               Long creatorId) {
        Class clazz = new Class();
        clazz.title = title;
        clazz.description = description;
        clazz.price = price;
        clazz.capacity = capacity;
        clazz.startDate = startDate;
        clazz.endDate = endDate;
        clazz.creatorId = creatorId;
        clazz.status = ClassStatus.DRAFT;
        return clazz;
    }

    public void changeStatus(ClassStatus newStatus) {
        if (!this.status.canChangeTo(newStatus)) {
            throw new BusinessException(ErrorCode.CLASS_INVALID_STATUS_TRANSITION);
        }
        this.status = newStatus;
    }
}
