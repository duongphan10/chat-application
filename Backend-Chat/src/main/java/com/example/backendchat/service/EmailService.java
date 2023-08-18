package com.example.backendchat.service;

import com.example.backendchat.domain.dto.request.ForgotPasswordRequestDto;
import com.example.backendchat.domain.dto.response.CommonResponseDto;

public interface EmailService {
    CommonResponseDto sendVerificationForgotPassword(ForgotPasswordRequestDto requestDto);
    CommonResponseDto verificationForgotPassword(String email, String verificationCode);
}
