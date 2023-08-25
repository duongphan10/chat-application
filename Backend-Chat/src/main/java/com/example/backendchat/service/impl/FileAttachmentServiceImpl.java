package com.example.backendchat.service.impl;

import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.domain.entity.FileAttachment;
import com.example.backendchat.domain.entity.Message;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.FileAttachmentRepository;
import com.example.backendchat.repository.MessageRepository;
import com.example.backendchat.service.FileAttachmentService;
import com.example.backendchat.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@RequiredArgsConstructor
public class FileAttachmentServiceImpl implements FileAttachmentService {
    private final MessageRepository messageRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final UploadFileUtil uploadFileUtil;

    @Override
    public CompletableFuture<FileAttachment> create(String messageId, MultipartFile file) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Message.ERR_NOT_FOUND_ID, new String[]{messageId}));
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setFileName(file.getOriginalFilename());
        fileAttachment.setFileSize(file.getSize());
        fileAttachment.setFilePath(uploadFileUtil.uploadFile(file));
        fileAttachment.setMessage(message);
        fileAttachmentRepository.save(fileAttachment);
        return CompletableFuture.completedFuture(fileAttachment);
    }

    @Override
    public void deleteByMessageId(String messageId) {

    }
}
