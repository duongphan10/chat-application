package com.example.backendchat.service.impl;

import com.example.backendchat.constant.*;
import com.example.backendchat.domain.dto.common.DataMailDto;
import com.example.backendchat.domain.dto.request.*;
import com.example.backendchat.domain.dto.response.CommonResponseDto;
import com.example.backendchat.domain.dto.response.LoginResponseDto;
import com.example.backendchat.domain.dto.response.TokenRefreshResponseDto;
import com.example.backendchat.domain.dto.response.UserDto;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.domain.mapper.UserMapper;
import com.example.backendchat.exception.AlreadyExistException;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.exception.UnauthorizedException;
import com.example.backendchat.repository.RoleRepository;
import com.example.backendchat.repository.UserRepository;
import com.example.backendchat.security.UserPrincipal;
import com.example.backendchat.security.jwt.JwtTokenProvider;
import com.example.backendchat.service.AuthService;
import com.example.backendchat.util.CheckLoginRequest;
import com.example.backendchat.util.RandomString;
import com.example.backendchat.util.SendMailUtil;
import com.example.backendchat.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = null;
            if (CheckLoginRequest.isEmail(request.getUsername())) {
                User user = userRepository.findUserByEmail(request.getUsername()).orElseThrow(
                        () -> new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME));
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
            } else {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            }
            if (authentication == null) {
                throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateToken(userPrincipal, Boolean.FALSE);
            String refreshToken = jwtTokenProvider.generateToken(userPrincipal, Boolean.TRUE);
            return new LoginResponseDto(accessToken, refreshToken, userPrincipal.getId(), authentication.getAuthorities());
        } catch (InternalAuthenticationServiceException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_USERNAME);
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_PASSWORD);
        }
    }

    @Override
    public UserDto register(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new AlreadyExistException(ErrorMessage.User.ERR_ALREADY_EXIST_USER,
                    new String[]{"email: " + userCreateDto.getEmail()});
        }
        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new AlreadyExistException(ErrorMessage.User.ERR_ALREADY_EXIST_USER,
                    new String[]{"username: " + userCreateDto.getUsername()});
        }
        User user = userMapper.toUser(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleRepository.findByRoleName(RoleConstant.USER));
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public TokenRefreshResponseDto refresh(TokenRefreshRequestDto request) {
        return null;
    }

    @Override
    public CommonResponseDto logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return new CommonResponseDto(true, MessageConstant.SUCCESSFULLY_LOGOUT);
    }

    @Override
    public CommonResponseDto createPassword(NewPasswordRequestDto newPasswordRequestDto) {
        User user = userRepository.findUserByEmail(newPasswordRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_EMAIL, new String[]{newPasswordRequestDto.getEmail()}) );
        user.setPassword(passwordEncoder.encode(newPasswordRequestDto.getPassword()));
        userRepository.save(user);
        return new CommonResponseDto(true, MessageConstant.CREATE_NEW_PASSWORD_SUCCESSFULLY);
    }

}
