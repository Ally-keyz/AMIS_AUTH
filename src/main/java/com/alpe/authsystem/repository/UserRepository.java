package com.alpe.authsystem.repository;

import com.alpe.authsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByDocumentNumber(String documentNumber);

    Optional<User> findByResetToken(String resetToken);
}