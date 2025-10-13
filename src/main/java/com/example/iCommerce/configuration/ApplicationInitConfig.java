package com.example.iCommerce.configuration;

import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.Role;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
              if(userRepository.findByEmail("admin@gmail.com").isEmpty()){
                  User user = new User().builder()
                          .email("admin@gmail.com")
                          .password(passwordEncoder.encode("Admin@123456"))
                          .user_type(Role.ADMIN.name())
                          .reputation(100)
                          .build();
                  userRepository.save(user);
              }
              if(userRepository.findByEmail("partnership@gmail.com").isEmpty()){
                  User user = new User().builder()
                          .email("partnership@gmail.com")
                          .password(passwordEncoder.encode("partnership@123456"))
                          .user_type(Role.PARTNERSHIP.name())
                          .reputation(100)
                          .build();
                  userRepository.save(user);
              }
        };
    }
}
