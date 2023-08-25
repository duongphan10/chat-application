package com.example.backendchat.controller;

import com.example.backendchat.base.RestApiV1;
import com.example.backendchat.base.VsResponseUtil;
import com.example.backendchat.constant.UrlConstant;
import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.security.CurrentUser;
import com.example.backendchat.security.UserPrincipal;
import com.example.backendchat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestApiV1
public class MessageController {
    private final MessageService messageService;

    @Tag(name = "message-controller")
    @Operation(summary = "API create new message")
    @PostMapping(value = UrlConstant.Message.SEND_MESSAGE_TO_OTHER, consumes = "multipart/form-data")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> sendMessageToOtherById(@Valid @ModelAttribute MessageRequestDto messageRequestDto,
                                                    @Parameter(name = "user", hidden = true)
                                                    @CurrentUser UserPrincipal user) {
        return VsResponseUtil.success(messageService.sendMessage(user.getId(), messageRequestDto));
    }

    @Tag(name = "message-controller")
    @Operation(summary = "API get message with other id")
    @GetMapping(value = UrlConstant.Message.GET_MESSAGES_BY_OTHER_BY_ID)
    public ResponseEntity<?> getMessagesBySenderIdAndReceiverId(@Valid @ParameterObject PaginationFullRequestDto paginationFullRequestDto,
                                                                @Parameter(name = "user", hidden = true)
                                                                @CurrentUser UserPrincipal user,
                                                                @PathVariable String receiverId) {
        return VsResponseUtil.success(messageService
                .getMessagesBySenderIdAndReceiverId(paginationFullRequestDto, user.getId(), receiverId));
    }
}
