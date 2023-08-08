package com.example.backendchat.controller;

import com.example.backendchat.base.RestApiV1;
import com.example.backendchat.base.VsResponseUtil;
import com.example.backendchat.constant.UrlConstant;
import com.example.backendchat.domain.dto.request.ForgotPasswordRequestDto;
import com.example.backendchat.domain.dto.request.LoginRequestDto;
import com.example.backendchat.domain.dto.request.UserCreateDto;
import com.example.backendchat.service.AuthService;
import com.example.backendchat.validator.annotation.ValidFileImage;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@Validated
@RestApiV1
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "API Login")
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        return VsResponseUtil.success(authService.login(request));
    }

    @Operation(summary = "API Logout")
    @GetMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response, Authentication authentication) {
        return VsResponseUtil.success(authService.logout(request, response, authentication));
    }

    @Operation(summary = "API Register")
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto userCreateDto) {
        return VsResponseUtil.success(authService.register(userCreateDto));
    }

    @Operation(summary = "API Forgot Password")
    @PostMapping(UrlConstant.Auth.RESET_PASSWORD)
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        return VsResponseUtil.success(authService.forgotPassword(forgotPasswordRequestDto));
    }
}
