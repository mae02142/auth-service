package com.bookpli.auth_service.controller;

import com.bookpli.auth_service.common.response.BaseResponse;
import com.bookpli.auth_service.manager.TokenManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/authservice")
public class TokenController {

    private final TokenManager tokenManager;

    public TokenController(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @GetMapping("/accessToken/{spotifyId}")
    public BaseResponse<String> getAccessToken(@PathVariable String spotifyId) {
        String accessToken = tokenManager.getAccessToken(spotifyId);
        System.out.println(accessToken);
        return new BaseResponse<>(accessToken);
    }
}