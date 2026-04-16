package com.alpe.authsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // =============================
    //  WELCOME EMAIL (Registration)
    // =============================
    public void sendWelcomeEmail(String toEmail, String name, String rawPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Welcome! Your Account Has Been Created");

            String html = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                        <h2 style="color: #2c3e50;">Welcome, %s! 👋</h2>
                        <p>Your account has been successfully created. Below are your login credentials:</p>
                        <div style="background: #f4f6f8; padding: 16px; border-radius: 6px; margin: 16px 0;">
                            <p style="margin: 0;"><strong>Email:</strong> %s</p>
                            <p style="margin: 8px 0 0;"><strong>Temporary Password:</strong> <code style="background:#e8f0fe;padding:2px 6px;border-radius:4px;">%s</code></p>
                        </div>
                        <p style="color: #e74c3c;"><strong>Important:</strong> Please change your password after your first login.</p>
                        <a href="%s/forgot-password" style="display:inline-block;margin-top:12px;padding:10px 20px;background:#3498db;color:#fff;text-decoration:none;border-radius:5px;">Reset password</a>
                        <p style="margin-top: 24px; font-size: 12px; color: #999;">If you did not request this account, please ignore this email.</p>
                    </div>
                    """.formatted(name, toEmail, rawPassword, frontendUrl);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send welcome email: " + e.getMessage(), e);
        }
    }

    // =============================
    //  FORGOT PASSWORD EMAIL
    // =============================
    public void sendPasswordResetEmail(String toEmail, String name, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

            String html = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                        <h2 style="color: #2c3e50;">Password Reset Request</h2>
                        <p>Hi <strong>%s</strong>,</p>
                        <p>We received a request to reset your password. Click the button below to choose a new password. This link expires in <strong>30 minutes</strong>.</p>
                        <a href="%s" style="display:inline-block;margin-top:12px;padding:10px 24px;background:#e74c3c;color:#fff;text-decoration:none;border-radius:5px;">Reset My Password</a>
                        <p style="margin-top:16px;">Or copy this link into your browser:</p>
                        <p style="word-break:break-all;color:#3498db;">%s</p>
                        <p style="margin-top: 24px; font-size: 12px; color: #999;">If you did not request a password reset, you can safely ignore this email.</p>
                    </div>
                    """.formatted(name, resetLink, resetLink);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage(), e);
        }
    }
}