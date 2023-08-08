package com.example.backendchat.service;

import com.example.backendchat.domain.dto.pagination.PaginationFullRequestDto;
import com.example.backendchat.domain.dto.pagination.PaginationResponseDto;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.domain.dto.response.MessageResponseDto;

public interface MessageService {
    MessageResponseDto sendMessage(String meId, MessageRequestDto messageRequestDto);

    PaginationResponseDto<MessageResponseDto> getMessagesBySenderIdAndReceiverId(
            PaginationFullRequestDto paginationFullRequestDto,
            String meId, String receiverId);
}
