package com.bookpli.auth_service.controller;

import com.bookpli.auth_service.common.response.BaseResponse;
import com.bookpli.auth_service.dto.UserDTO;
import com.bookpli.auth_service.service.AuthService;
import com.bookpli.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserFeignController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/review")
    public BaseResponse<List<UserDTO>> getUserInfoForReview(List<Long> userIds, @RequestHeader("Authorization") String token){
        System.out.println("오픈페인 토큰 : "+ token);
        System.out.println("오픈페인 유저 아이디들 "+ userIds);

        jwtService.verifyToken(token);

        List<UserDTO> userDTOS = userIds.stream()
                .map(authService::getUserProfile)
                .collect(Collectors.toList());
        return new BaseResponse<>(userDTOS);
    }

}
