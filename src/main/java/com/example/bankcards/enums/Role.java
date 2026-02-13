package com.example.bankcards.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    @JsonCreator
    public static Role create(String str){
        return Role.valueOf(str);
    }
}
