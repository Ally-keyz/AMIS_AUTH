package com.alpe.authsystem.dto;

import org.jspecify.annotations.Nullable;

import lombok.Data;

@Data
public class RegisterRequest {
    private String documentType;
    private String documentNumber;
    private String name;
    private String email;
    public @Nullable CharSequence getPassword() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }
}