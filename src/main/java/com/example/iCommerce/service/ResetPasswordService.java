package com.example.iCommerce.service;


import com.example.iCommerce.entity.PasswordResetToken;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.AccountType;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.repository.PasswordResetTokenRepository;
import com.example.iCommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResetPasswordService {

    PasswordResetTokenRepository tokenRepo;
    UserRepository userRepo;
    EmailService emailService;
    PasswordEncoder passwordEncoder;

    @Transactional
    public void requestReset(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        if(Objects.equals(user.getAccount_type(), AccountType.SOCIAL.name())){
            throw new AppException(ErrorCode.IS_SOCIAL_ACCOUNT);
        }
        // Tạo mã 6 số
        String token = String.format("%06d", new SecureRandom().nextInt(999999));

        // Xóa mã cũ (nếu có)
        tokenRepo.deleteByEmail(email);

        PasswordResetToken resetToken = new PasswordResetToken(
                null, email, token, LocalDateTime.now().plusMinutes(5)
        );
        tokenRepo.save(resetToken);

        emailService.sendResetToken(email, token);
    }

    public boolean verifyToken(String email, String token) {
        return tokenRepo.findByEmailAndToken(email, token)
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        if (!verifyToken(email, token)) {
            throw new RuntimeException("Mã xác nhận không hợp lệ hoặc đã hết hạn.");
        }

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        tokenRepo.deleteByEmail(email);
    }
}
