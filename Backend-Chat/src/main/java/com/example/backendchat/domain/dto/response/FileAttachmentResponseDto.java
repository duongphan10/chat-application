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
public class FileAttachmentResponseDto {
    private String id;
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String createdDate;
    private String lastModifiedDate;
    private String messageId;
}
