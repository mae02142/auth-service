package com.bookpli.auth_service.filter;

import com.bookpli.auth_service.service.CustomPrincipal;
import com.bookpli.auth_service.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("JwtFilter 시작: 요청 URI = {}", requestURI);

        // 필터 제외 경로 명확히 설정
        if (requestURI.startsWith("/authservice/")) {
            log.debug("필터 제외 경로: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 먼저 JWT 확인
        String jwt = extractJwtFromHeader(request);
        log.debug("Authorization 헤더에서 JWT 추출: {}", jwt);

        // 헤더에 없다면 쿠키에서 확인
        if (jwt == null) {
            jwt = extractJwtFromCookies(request);
            log.debug("쿠키에서 JWT 추출: {}", jwt);
        }

        log.debug("추출된 JWT: {}", jwt);

        if (jwt == null) {
            log.debug("JWT 토큰이 존재하지 않습니다. 401 Unauthorized 반환");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"로그인이 필요합니다.\"}");
            return;
        }

        try {
            Claims claims = jwtService.verifyToken(jwt);
            setAuthentication(claims);
        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"유효하지 않은 토큰입니다.\"}");
            return;
        }

        filterChain.doFilter(request, response);
        log.debug("JwtFilter 종료: 요청 처리 완료");
    }

    /**
     * 쿠키에서 JWT 추출
     */
    private String extractJwtFromCookies(HttpServletRequest request) {
        //쿠키에서 토큰 추출
        return Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Header에서 JWT 추출
     */
    private String extractJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * SecurityContext에 사용자 인증 정보 저장
     */
    private void setAuthentication(Claims claims) {
        // 사용자 정보 추출
        String spotifyId = claims.get("spotifyId", String.class);
        Long userId = claims.get("userId", Long.class);

        // 사용자 정보 담기
        CustomPrincipal principal = new CustomPrincipal(spotifyId, userId);

        // 권한 설정
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        // SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
