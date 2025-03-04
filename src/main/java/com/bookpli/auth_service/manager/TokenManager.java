package com.bookpli.auth_service.manager;

import com.bookpli.auth_service.service.JwtService;
import com.bookpli.auth_service.service.SpotifyApiService;
import com.bookpli.auth_service.service.TokenCacheService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenManager {

    private final TokenCacheService tokenCacheService;
    private final SpotifyApiService spotifyApiService;
    private final JwtService jwtService;

    public TokenManager(TokenCacheService tokenCacheService, SpotifyApiService spotifyApiService, JwtService jwtService) {
        this.tokenCacheService = tokenCacheService;
        this.spotifyApiService = spotifyApiService;
        this.jwtService = jwtService;
    }

    /**
     * Access Token 조회 (Spotify ID 사용)
     * @param spotifyId 사용자 Spotify ID
     * @return 유효한 Access Token
     */
    public String getAccessToken(String spotifyId) {
        return tokenCacheService.getAccessToken(spotifyId, () -> refreshAccessToken(spotifyId));
    }

    /**
     * Access Token 갱신
     * @param spotifyId 사용자 Spotify ID
     * @return 새로 갱신된 Access Token
     */
    private String refreshAccessToken(String spotifyId) {
        String refreshToken = spotifyApiService.getRefreshToken(spotifyId);
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh Token is null or empty for Spotify ID: " + spotifyId);
        }

        Map<String, String> newTokens = spotifyApiService.refreshAccessToken(refreshToken);
        String newAccessToken = newTokens.get("access_token");

        if (newAccessToken == null || newAccessToken.isEmpty()) {
            throw new IllegalStateException("Failed to retrieve Access Token for Spotify ID: " + spotifyId);
        }

        return newAccessToken; // 새로 갱신된 Access Token 반환
    }

    /**
     * JWT 검증 후 Spotify ID 추출
     * @param jwt 사용자 JWT
     * @return Spotify ID
     */
    public String extractSpotifyIdFromJwt(String jwt) {
        Claims claims = jwtService.verifyToken(jwt);
        return claims.get("spotifyId", String.class);
    }
}
