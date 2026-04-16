package com.alpe.authsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentType;

    @Column(unique = true)
    private String documentNumber;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;
}