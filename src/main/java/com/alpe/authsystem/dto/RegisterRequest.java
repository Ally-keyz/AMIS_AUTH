package com.alpe.authsystem.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String documentType;
    private String documentNumber;
    private String name;
    private String email;
}