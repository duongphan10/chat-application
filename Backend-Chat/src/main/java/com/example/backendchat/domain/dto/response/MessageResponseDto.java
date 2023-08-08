package com.example.backendchat.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private String id;
    private String message;
    private String senderId;
    private String receiverId;
    private String createdDate;
    private String lastModifiedDate;
}
