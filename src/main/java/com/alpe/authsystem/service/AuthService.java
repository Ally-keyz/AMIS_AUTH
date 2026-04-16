package com.alpe.authsystem.service;

import com.alpe.authsystem.dto.RegisterRequest;
import com.alpe.authsystem.entity.User;
import com.alpe.authsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // =============================
    //  REGISTER
    //  - Auto-generates a password
    //  - Emails credentials to user
    // =============================
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Generate a secure random password
        String rawPassword = generateSecurePassword();

        User user = new User();
        user.setDocumentType(request.getDocumentType());
        user.setDocumentNumber(request.getDocumentNumber());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(user);

        // Send welcome email with the raw (plain-text) password
        emailService.sendWelcomeEmail(request.getEmail(), request.getName(), rawPassword);

        return "Registration successful. Your password has been sent to " + request.getEmail();
    }

    // =============================
    //  LOGIN
    // =============================
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return "Login successful. Welcome, " + user.getName();
    }

    // =============================
    //  FORGOT PASSWORD
    //  - Generates a reset token
    //  - Saves it with expiry
    //  - Emails a reset link to the user
    // =============================
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with that email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30)); // expires in 30 min
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, user.getName(), token);

        return "A password reset link has been sent to " + email;
    }

    // =============================
    //  RESET PASSWORD
    //  - Validates token + expiry
    //  - Updates the password
    //  - Clears the reset token
    // =============================
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null ||
                LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new RuntimeException("Reset token has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return "Password has been reset successfully. You can now log in.";
    }

    // =============================
    //  HELPERS
    // =============================

    /**
     * Generates a 12-character password with:
     * - uppercase letters
     * - lowercase letters
     * - digits
     * - special characters
     */
    private String generateSecurePassword() {
        String upper  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower  = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%&*!";
        String all = upper + lower + digits + special;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Guarantee at least one character from each group
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill remaining 8 characters randomly
        for (int i = 4; i < 12; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        // Shuffle to avoid predictable pattern (first 4 chars always fixed-type)
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}