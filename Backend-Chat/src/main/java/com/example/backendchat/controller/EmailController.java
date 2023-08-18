package com.example.backendchat.controller;

import com.example.backendchat.base.RestApiV1;
import com.example.backendchat.base.VsResponseUtil;
import com.example.backendchat.constant.UrlConstant;
import com.example.backendchat.domain.dto.request.ForgotPasswordRequestDto;
import com.example.backendchat.domain.dto.response.CommonResponseDto;
import com.example.backendchat.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestApiV1
public class EmailController {
    private final EmailService emailService;
    @Tag(name = "email-controller")
    @Operation(summary = "API send verification forgot password")
    @PostMapping(UrlConstant.Email.SEND_VERIFICATION_FORGOT_PASSWORD)
    public ResponseEntity<?> sendVerificationForgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        return VsResponseUtil.success(emailService.sendVerificationForgotPassword(forgotPasswordRequestDto));
    }

    @Tag(name = "email-controller")
    @Operation(summary = "API verify forgot password")
    @PostMapping(value = UrlConstant.Email.VERIFICATION_FORGOT_PASSWORD)
    public ResponseEntity<?>  verificationForgotPassword(@RequestParam String email,@RequestParam String code) {
        return VsResponseUtil.success(emailService.verificationForgotPassword(email, code));
    }
}
