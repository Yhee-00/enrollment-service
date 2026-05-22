package com.example.enrollment.presentation;

import com.example.enrollment.application.ClassService;
import com.example.enrollment.domain.clazz.ClassStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @PostMapping
    public ResponseEntity<ClassResponse> create(
            @Valid @RequestBody ClassCreateRequest request,
            @RequestHeader("X-User-Id") Long creatorId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classService.create(request, creatorId));
    }

    @GetMapping
    public ResponseEntity<List<ClassResponse>> findAll(
            @RequestParam(required = false) ClassStatus status) {
        return ResponseEntity.ok(classService.findAll(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassDetailResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClassResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClassStatusRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(classService.changeStatus(id, request.getStatus(), userId));
    }
}
