package com.example.iCommerce.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Danh sách API cho phép POST mà không cần auth
    private final String[] POST_PUBLIC = {
            "/user", "/auth/login", "/auth/introspect", "/auth/logout",
            "/auth/refresh", "/product/search", "/product/filter",
            "/auth/google-login", "/auth/facebook-login",
            "/api/momo/create", "/api/momo/ipn-handler",
            "/auth/forgot-password", "/auth/verify-reset-token",
            "/auth/reset-password"
    };

    // Danh sách API cho phép GET mà không cần auth
    private final String[] GET_PUBLIC = {
            "/product", "/product/{id}", "/product/item", "/images/{imageName}",
            "/product-variant/price-range", "/category", "/brand", "/poster",
            "/product-variant/{id}", "/rating/{id}", "/rating/{id}/ratings",
            "/api/momo/ipn-handler", "/gift/{id}/product-variant"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    // Mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // Cấu hình Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, POST_PUBLIC).permitAll()
                        .requestMatchers(HttpMethod.GET, GET_PUBLIC).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    // Chuyển đổi JWT -> GrantedAuthorities
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    // Cho phép CORS cho cả localhost và domain Vercel
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.asList(
                "https://ad-cosmetic.vercel.app",
                "https://cosmetic-sigma.vercel.app",
                "http://localhost:3000",
                "http://localhost:3001"
        );
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Đảm bảo Spring MVC cũng bật CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(
                        "https://ad-cosmetic.vercel.app",
                        "https://cosmetic-sigma.vercel.app",
                        "http://localhost:3000",
                        "http://localhost:3001"
                );
            }
        };
    }
}
