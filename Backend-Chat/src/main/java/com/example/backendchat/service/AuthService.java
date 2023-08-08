package com.example.backendchat.service;

import com.example.backendchat.domain.dto.request.ForgotPasswordRequestDto;
import com.example.backendchat.domain.dto.request.LoginRequestDto;
import com.example.backendchat.domain.dto.request.TokenRefreshRequestDto;
import com.example.backendchat.domain.dto.request.UserCreateDto;
import com.example.backendchat.domain.dto.response.CommonResponseDto;
import com.example.backendchat.domain.dto.response.LoginResponseDto;
import com.example.backendchat.domain.dto.response.TokenRefreshResponseDto;
import com.example.backendchat.domain.dto.response.UserDto;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);

    UserDto register(UserCreateDto userCreateDto);

    TokenRefreshResponseDto refresh(TokenRefreshRequestDto request);

    CommonResponseDto logout(HttpServletRequest request,
                             HttpServletResponse response, Authentication authentication);

    CommonResponseDto forgotPassword(ForgotPasswordRequestDto requestDto);
}
