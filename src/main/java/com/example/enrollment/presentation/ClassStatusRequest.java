package com.example.enrollment.presentation;

import com.example.enrollment.domain.clazz.ClassStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ClassStatusRequest {

    @NotNull(message = "변경할 상태는 필수입니다.")
    private ClassStatus status;
}
