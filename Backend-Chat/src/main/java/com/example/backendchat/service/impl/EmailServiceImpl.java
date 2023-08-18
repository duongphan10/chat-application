package com.example.backendchat.service.impl;

import com.example.backendchat.BackendChatApplication;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.constant.MessageConstant;
import com.example.backendchat.constant.UrlConstant;
import com.example.backendchat.domain.dto.common.DataMailDto;
import com.example.backendchat.domain.dto.request.ForgotPasswordRequestDto;
import com.example.backendchat.domain.dto.response.CommonResponseDto;
import com.example.backendchat.domain.entity.Code;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.CodeRepository;
import com.example.backendchat.repository.UserRepository;
import com.example.backendchat.security.jwt.JwtTokenProvider;
import com.example.backendchat.service.EmailService;
import com.example.backendchat.util.RandomString;
import com.example.backendchat.util.SendMailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendMailUtil sendMailUtil;
    private final CodeRepository codeRepository;

    public CommonResponseDto sendVerificationForgotPassword(ForgotPasswordRequestDto requestDto) {
        User user = userRepository.findUserByEmail(requestDto.getEmail()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{requestDto.getEmail()})
        );
        String verificationCode = RandomString.generateCode(CommonConstant.RANDOM_VERIFICATION_C0DE_LENGTH);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CommonConstant.VERIFICATION_CODE_EXPIRATION_MINUTES);

        Code code = codeRepository.findByUserId(user.getId());
        if (code != null) {
            code.setVerificationCode(verificationCode);
            code.setExpirationTime(expirationTime);
        }
        else {
            code = new Code(null, verificationCode, expirationTime, user);
        }
        codeRepository.save(code);

        Map<String, Object> props = new HashMap<>();
        props.put("appName", CommonConstant.APP_NAME);
        props.put("fullName", user.getFullName());
        props.put("expirationTime", CommonConstant.VERIFICATION_CODE_EXPIRATION_MINUTES);
        props.put("code", verificationCode);

        DataMailDto mail = new DataMailDto(user.getEmail(),
                MessageConstant.SUBJECT_MAIL_RESET_PASSWORD, null, props);
        try {
            sendMailUtil.sendEmailWithHTML(mail, "verify-forgot-password.html");
        } catch (Exception e) {
            log.error("Send mail failed for {}", e.getMessage());
        }
        return new CommonResponseDto(true, MessageConstant.VERIFY_FORGOT_PASSWORD);
    }

    @Override
    public CommonResponseDto verificationForgotPassword(String email,String verificationCode) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{email}) );
        Code code = codeRepository.findByUserId(user.getId());
        if (code != null) {
            if (!code.getVerificationCode().equals(verificationCode))
                return new CommonResponseDto(false, MessageConstant.VERIFY_FORGOT_PASSWORD_INVALID);

            if (!code.getExpirationTime().isAfter(LocalDateTime.now()))
                return new CommonResponseDto(false, MessageConstant.VERIFY_FORGOT_PASSWORD_EXPIRED);
        }
        return new CommonResponseDto(true, MessageConstant.VERIFY_FORGOT_PASSWORD_SUCCESSFULLY);
    }
}
