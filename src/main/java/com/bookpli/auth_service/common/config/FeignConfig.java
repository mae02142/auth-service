package com.bookpli.auth_service.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 여기에 JWT 토큰을 Auth Service로부터 받아오거나, 전달받은 토큰을 추가하는 로직을 작성합니다.
            String jwtToken = "Bearer " + "전달받은_JWT_토큰"; // 이 부분은 실제 토큰 관리 방식에 따라 동적으로 처리
            requestTemplate.header("Authorization", jwtToken);
        };
    }
}
