package com.example.backendchat.service;

import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.pagination.PaginationResponseDto;
import com.example.backendchat.domain.dto.request.ChangePasswordRequestDto;
import com.example.backendchat.domain.dto.request.UserUpdateDto;
import com.example.backendchat.domain.dto.response.CommonResponseDto;
import com.example.backendchat.domain.dto.response.UserDto;
import com.example.backendchat.security.UserPrincipal;

public interface UserService {

    UserDto getUserById(String userId);

    PaginationResponseDto<UserDto> getCustomers(PaginationFullRequestDto request);

    UserDto getCurrentUser(UserPrincipal principal);

    UserDto updateInfo(String userId, UserUpdateDto userUpdateDto);

    CommonResponseDto changePassword(String userId, ChangePasswordRequestDto passwordRequestDto);

    PaginationResponseDto<UserDto> getAllUserConversation(PaginationFullRequestDto paginationFullRequestDto, String meId);
}
