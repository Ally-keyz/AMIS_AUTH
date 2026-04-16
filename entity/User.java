package com.alpe.authsystem.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(unique = true)
    private String documentNumber;

    private String name;

    private String email;

    private String phoneNumber;

    private String documentType;

    private String password;

    private String resetToken;
} 