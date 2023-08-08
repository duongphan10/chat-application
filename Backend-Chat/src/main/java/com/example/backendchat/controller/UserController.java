package com.example.backendchat.controller;

import com.example.backendchat.base.RestApiV1;
import com.example.backendchat.base.VsResponseUtil;
import com.example.backendchat.constant.UrlConstant;
import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.request.ChangePasswordRequestDto;
import com.example.backendchat.domain.dto.request.UserUpdateDto;
import com.example.backendchat.security.CurrentUser;
import com.example.backendchat.security.UserPrincipal;
import com.example.backendchat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestApiV1
public class UserController {

    private final UserService userService;

    @Tag(name = "user-controller-admin")
    @Operation(summary = "API get user")
    @GetMapping(UrlConstant.User.GET_USER)
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return VsResponseUtil.success(userService.getUserById(userId));
    }

    @Tags({@Tag(name = "user-controller-admin"), @Tag(name = "user-controller")})
    @Operation(summary = "API get current user login")
    @GetMapping(UrlConstant.User.GET_CURRENT_USER)
    public ResponseEntity<?> getCurrentUser(@Parameter(name = "principal", hidden = true)
                                            @CurrentUser UserPrincipal principal) {
        return VsResponseUtil.success(userService.getCurrentUser(principal));
    }

    @Tag(name = "user-controller-admin")
    @Operation(summary = "API get all customer")
    @GetMapping(UrlConstant.User.GET_USERS)
    public ResponseEntity<?> getCustomers(@Valid @ParameterObject PaginationFullRequestDto requestDTO) {
        return VsResponseUtil.success(userService.getCustomers(requestDTO));
    }

    @Tag(name = "user-controller")
    @Operation(summary = "API update info user")
    @PatchMapping(value = UrlConstant.User.UPDATE_USER, consumes = "multipart/form-data")
    public ResponseEntity<?> updateUser(@Parameter(name = "principal", hidden = true)
                                        @CurrentUser UserPrincipal user,
                                        @Valid @ModelAttribute UserUpdateDto userUpdateDto) {
        return VsResponseUtil.success(userService.updateInfo(user.getId(), userUpdateDto));
    }
    @Tag(name = "user-controller")
    @Operation(summary = "API change password")
    @PostMapping(UrlConstant.User.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@Parameter(name = "principal", hidden = true)
                                        @CurrentUser UserPrincipal user,
                                        @Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        return VsResponseUtil.success(userService.changePassword(user.getId(), changePasswordRequestDto));
    }

    @Tag(name = "user-controller")
    @Operation(summary = "API get all user conversation")
    @GetMapping(value = UrlConstant.User.GET_ALL_USER_CONVERSATION)
    public ResponseEntity<?> getAllUserConversation(@Parameter(name = "user", hidden = true)
                                          @CurrentUser UserPrincipal user,
                                          @Valid @ParameterObject PaginationFullRequestDto paginationFullRequestDto) {
        return VsResponseUtil.success(userService.getAllUserConversation(paginationFullRequestDto, user.getId()));
    }
}
