package com.example.enrollment.domain.clazz;

public enum ClassStatus {
    DRAFT, OPEN, CLOSED;

    public boolean canChangeTo(ClassStatus next) {
        return switch (this){
            case DRAFT -> next == OPEN;
            case OPEN -> next == CLOSED;
            case CLOSED -> false;
        };
    }
}
