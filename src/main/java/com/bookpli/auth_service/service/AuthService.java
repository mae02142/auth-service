package com.bookpli.auth_service.service;

import com.bookpli.auth_service.dto.UserDTO;
import com.bookpli.auth_service.entity.User;
import com.bookpli.auth_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final SpotifyApiService spotifyApiService;
    private final TokenCacheService tokenCacheService;
    private final UserRepository userRepository;
    private final RandomNicknameService randomNicknameService;


    public AuthService(SpotifyApiService spotifyApiService, TokenCacheService tokenCacheService, UserRepository userRepository, JwtService jwtService, RandomNicknameService randomNicknameService) {
        this.spotifyApiService = spotifyApiService;
        this.tokenCacheService = tokenCacheService;
        this.userRepository = userRepository;
        this.randomNicknameService = randomNicknameService;
    }

    public Map<String, Object> processSpotifyCallback(String code) {
        // 1. Access Token 및 Refresh Token 요청
        Map<String, String> tokenResponse = spotifyApiService.requestSpotifyAccessToken(code);
        String accessToken = tokenResponse.get("access_token");
        String refreshToken = tokenResponse.get("refresh_token");

        // 2. Spotify 회원 정보 조회
        UserDTO userDTO = spotifyApiService.fetchSpotifyUserProfile(accessToken);
        System.out.println("회원 정보 확인 : " + userDTO);

        // 3. DB 저장 또는 업데이트
        User user = userRepository.findBySpotifyId(userDTO.getSpotifyId())
                .map(existingUser -> {
                    if (refreshToken != null && !refreshToken.equals(existingUser.getRefreshToken())) {
                        existingUser.updateRefreshToken(refreshToken);
                    }
                    return existingUser;
                })
                .orElseGet(() -> {
                    String randomNick = randomNicknameService.generateNickname();
                    User newUser = userDTO.toEntity();
                    newUser.updateNickName(randomNick);
                    newUser.updateRefreshToken(refreshToken);
                    return userRepository.save(newUser);
                });

        // 4. Access Token 캐싱
        tokenCacheService.updateAccessToken(user.getSpotifyId(), accessToken);

        // 5. Map으로 userId와 spotifyId 반환
        return Map.of(
                "userId", user.getUserId(),
                "spotifyId", user.getSpotifyId()
        );
    }

    public Optional<User> findBySpotifyId(String spotifyId) {
        return userRepository.findBySpotifyId(spotifyId);
    }
}
