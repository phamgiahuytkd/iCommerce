package com.example.iCommerce.service;

import com.example.iCommerce.dto.request.*;
import com.example.iCommerce.dto.response.AuthenticationResponse;
import com.example.iCommerce.dto.response.IntrospectResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.InvalidatedToken;
import com.example.iCommerce.entity.Tracking;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.Role;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.repository.InvalidatedTokenRepository;
import com.example.iCommerce.repository.TrackingRepository;
import com.example.iCommerce.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrackingService {
    TrackingRepository trackingRepository;

    public void  tracking(String created_by, String key, String name) {



        if(created_by != null) {
            Tracking tracking = Tracking.builder()
                    .created_by(created_by)
                    .created_date(LocalDateTime.now())
                    .action_key(key)
                    .action_name(name)
                    .build();

            trackingRepository.save(tracking);
        }

    }
















}
