package com.example.backendchat.service;

import com.example.backendchat.domain.entity.FileAttachment;
import org.springframework.web.multipart.MultipartFile;

public interface FileAttachmentService {
    FileAttachment create(String messageId, MultipartFile file);
    void deleteByMessageId(String messageId);
}
