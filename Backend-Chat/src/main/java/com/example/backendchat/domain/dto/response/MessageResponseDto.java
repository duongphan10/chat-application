package com.example.backendchat.domain.dto.response;

import com.example.backendchat.domain.entity.FileAttachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private String id;
    private String message;
    private String senderId;
    private String receiverId;
    private List<FileAttachmentResponseDto> fileAttachments;
    private String createdDate;
    private String lastModifiedDate;
}
