package com.example.backendchat.domain.dto.request;

import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.domain.dto.common.DateAuditingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageRequestDto extends DateAuditingDto {
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String message;
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String receiverId;
}
