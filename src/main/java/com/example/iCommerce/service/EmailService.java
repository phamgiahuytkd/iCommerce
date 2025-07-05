package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.repository.AddressRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetToken(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã xác nhận đặt lại mật khẩu");
        message.setText("Mã xác nhận của bạn là: " + token + "\nMã có hiệu lực trong 5 phút.");
        mailSender.send(message);
    }












}
