package com.example.backendchat.domain.mapper;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.domain.dto.response.MessageResponseDto;
import com.example.backendchat.domain.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageMapper {
    @Mappings({
            @Mapping(target = "message", source = "message")
    })
    Message messageRequestDtoToMessage(MessageRequestDto messageRequestDto);

    @Mappings({
            @Mapping(target = "senderId", source = "sender.id"),
            @Mapping(target = "receiverId", source = "receiver.id"),
            @Mapping(target = "createdDate", dateFormat = CommonConstant.PATTERN_DATE_TIME),
            @Mapping(target = "lastModifiedDate", dateFormat = CommonConstant.PATTERN_DATE_TIME),
    })
    MessageResponseDto messageToMessageResponseDto(Message message);

    List<MessageResponseDto> messagesToMessageResponseDtos(List<Message> messages);
}
