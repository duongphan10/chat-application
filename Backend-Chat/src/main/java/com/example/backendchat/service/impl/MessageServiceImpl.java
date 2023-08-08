package com.example.backendchat.service.impl;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.constant.SortByDataConstant;
import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.pagination.PaginationResponseDto;
import com.example.backendchat.domain.dto.pagination.PagingMeta;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.domain.dto.response.MessageResponseDto;
import com.example.backendchat.domain.entity.Message;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.domain.mapper.MessageMapper;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.MessageRepository;
import com.example.backendchat.repository.UserRepository;
import com.example.backendchat.service.MessageService;
import com.example.backendchat.util.DateTimeUtil;
import com.example.backendchat.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final Validator validator;
    @Override
    public MessageResponseDto sendMessage(String meId, MessageRequestDto messageRequestDto) {
        Set<ConstraintViolation<MessageRequestDto>> violations = validator.validate(messageRequestDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        User sender = userRepository.findById(meId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{meId}));
        User receiver = userRepository.findById(messageRequestDto.getReceiverId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{messageRequestDto.getReceiverId()}));
        Message message = messageMapper.messageRequestDtoToMessage(messageRequestDto);
        message.setSender(sender);
        message.setReceiver(receiver);
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = messageMapper.messageToMessageResponseDto(message);
        messageResponseDto.setCreatedDate(DateTimeUtil.toString(message.getCreatedDate()));
        messageResponseDto.setLastModifiedDate(DateTimeUtil.toString(message.getLastModifiedDate()));

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
        return new PaginationResponseDto<>(meta , messageResponseDtoList);
    }
}
