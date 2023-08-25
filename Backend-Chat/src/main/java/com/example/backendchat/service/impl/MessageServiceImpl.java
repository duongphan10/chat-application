package com.example.backendchat.service.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.constant.SortByDataConstant;
import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.pagination.PaginationResponseDto;
import com.example.backendchat.domain.dto.pagination.PagingMeta;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.domain.dto.response.MessageResponseDto;
import com.example.backendchat.domain.entity.FileAttachment;
import com.example.backendchat.domain.entity.Message;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.domain.mapper.MessageMapper;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.MessageRepository;
import com.example.backendchat.repository.UserRepository;
import com.example.backendchat.service.FileAttachmentService;
import com.example.backendchat.service.MessageService;
import com.example.backendchat.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final Validator validator;
    private final FileAttachmentService fileAttachmentService;
    private final SocketIOServer server;

    @Override
    public MessageResponseDto sendMessage(String meId, MessageRequestDto messageRequestDto) {
//        Set<ConstraintViolation<MessageRequestDto>> violations = validator.validate(messageRequestDto);
//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(violations);
//        }
        User sender = userRepository.findById(meId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{meId}));
        User receiver = userRepository.findById(messageRequestDto.getReceiverId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{messageRequestDto.getReceiverId()}));
        Message message = messageMapper.messageRequestDtoToMessage(messageRequestDto);
        message.setSender(sender);
        message.setReceiver(receiver);
        messageRepository.save(message);

        List<CompletableFuture<FileAttachment>> fileAttachmentFutures = new ArrayList<>();
        if (messageRequestDto.getFileAttachments() != null && !messageRequestDto.getFileAttachments().isEmpty()) {
            for (MultipartFile file : messageRequestDto.getFileAttachments()) {
                CompletableFuture<FileAttachment> fileAttachmentFuture = fileAttachmentService.create(message.getId(), file);
                fileAttachmentFutures.add(fileAttachmentFuture);
            }
        }

        // Wait for all file attachment futures to complete
        CompletableFuture<Void> allFileAttachmentFuture = CompletableFuture.allOf(
                fileAttachmentFutures.toArray(new CompletableFuture[0])
        );

        allFileAttachmentFuture.thenRun(() -> {
            List<FileAttachment> fileAttachments = fileAttachmentFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // Set the file attachments and save the message again
            message.setFileAttachments(fileAttachments);
            messageRepository.save(message);
        });

        MessageResponseDto messageResponseDto = messageMapper.messageToMessageResponseDto(message);
        //messageResponseDto.setCreatedDate(DateTimeUtil.toString(message.getCreatedDate()));
        //messageResponseDto.setLastModifiedDate(DateTimeUtil.toString(message.getLastModifiedDate()));

        // Server send message
        server.getRoomOperations(sender.getId())
                .sendEvent(CommonConstant.Event.SERVER_SEND_MESSAGE, messageResponseDto);
        server.getRoomOperations(messageResponseDto.getReceiverId())
                .sendEvent(CommonConstant.Event.SERVER_SEND_MESSAGE, messageResponseDto);
        return messageResponseDto;
    }

    @Override
    public PaginationResponseDto<MessageResponseDto> getMessagesBySenderIdAndReceiverId(
            PaginationFullRequestDto paginationFullRequestDto, String meId, String receiverId) {
        paginationFullRequestDto.setIsAscending(false);
        paginationFullRequestDto.setPageSize(CommonConstant.NUM_OF_MESSAGES_PER_PAGE_DEFAULT);


        Pageable pageable = PaginationUtil.buildPageable(paginationFullRequestDto, SortByDataConstant.Message);
        Page<Message> messages = messageRepository.getMessagesBySenderIdAndReceiverId(meId, receiverId, pageable);

        PagingMeta meta = PaginationUtil
                .buildPagingMeta(paginationFullRequestDto, SortByDataConstant.Message, messages);

        List<MessageResponseDto> messageResponseDtoList =
                messageMapper.messagesToMessageResponseDtos(messages.getContent());
        return new PaginationResponseDto<>(meta, messageResponseDtoList);
    }
}
