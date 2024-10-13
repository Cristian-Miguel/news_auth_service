package com.auth.auth_service.shared.constant;

import lombok.Getter;

@Getter
public enum SystemConstant {
    MAX_FAILED_ATTEMPTS(5),
    LOCK_DURATION_MINUTES(120)
    ;

    private int value;

    private SystemConstant(int value){
        this.value = value;
    }
}
