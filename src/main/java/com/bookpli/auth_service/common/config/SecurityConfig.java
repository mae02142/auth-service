package com.bookpli.auth_service.common.config;

import com.bookpli.auth_service.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API 특성상 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 경로 기반 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/authservice/**",
                                "/favicon.ico"
                        ).permitAll() // 인증 없이 접근 가능
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )

                // JWT 필터를 Security 필터 체인에 추가
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 세션 관리: Stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 도메인 설정
        configuration.addAllowedOriginPattern("http://localhost:3000"); // 로컬 개발 환경
//        configuration.addAllowedOriginPattern("https://auth-service.example.com"); // 배포 환경

        // 허용할 HTTP 메서드
        configuration.addAllowedMethod("*");

        // 허용할 헤더
        configuration.addAllowedHeader("*");

        // 인증 정보 (쿠키, Authorization 헤더) 허용
        configuration.setAllowCredentials(true);

        // CORS 설정 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
