package com.example.backendchat.service;

import com.example.backendchat.domain.entity.FileAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface FileAttachmentService {
    CompletableFuture<FileAttachment> create(String messageId, MultipartFile file);
    void deleteByMessageId(String messageId);
}
