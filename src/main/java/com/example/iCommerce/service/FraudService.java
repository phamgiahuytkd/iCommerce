package com.example.iCommerce.service;

import com.example.iCommerce.dto.request.FraudRequest;
import com.example.iCommerce.dto.response.FraudResponse;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class FraudService {

    private final WebClient webClient;
    private final UserRepository userRepository;

    // Constructor dùng để Spring inject UserRepository
    @Autowired
    public FraudService(UserRepository userRepository, @Value("${fraud.api.url}") String fraudApiUrl) {
        this.userRepository = userRepository;
        this.webClient = WebClient.builder()
                .baseUrl(fraudApiUrl)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public FraudResponse predictFraud(FraudRequest request) {

        User user = userRepository.findById(request.getUser_id()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        int accountAgeDays = (int) ChronoUnit.DAYS.between(user.getCreate_day().toLocalDate(), LocalDate.now());
        int customerAge = Period.between(user.getDate_of_birth(), LocalDate.now()).getYears();

        log.info("Tuổi khách hàng: {}", customerAge);
        log.info("Tuổi tài khoản: {}", accountAgeDays);

        request.setCustomerAge(customerAge);
        request.setAccountAgeDays(accountAgeDays);

        FraudResponse response = webClient.post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FraudResponse.class)
                .block(); // đồng bộ

        // ⚙️ Quy đổi fraudScore (0–1) sang phần trăm (0–100)
        assert response != null;
        double fraudScorePercent = response.getProbability();
        double reputation = 100-user.getReputation();

        // ✅ Tính điểm tổng hợp: 70% fraud score + 30% reputation
        double finalScore = (fraudScorePercent * 0.7) + reputation * 0.003 ;

        // ✅ Gán lại vào response (để frontend hiển thị)
        response.setProbability(finalScore);

        return response;
    }
}
