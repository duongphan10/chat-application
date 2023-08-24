package com.example.backendchat.domain.mapper;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.domain.dto.response.FileAttachmentResponseDto;
import com.example.backendchat.domain.dto.response.MessageResponseDto;
import com.example.backendchat.domain.entity.FileAttachment;
import com.example.backendchat.domain.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageMapper extends FileAttachmentMapper{
    @Mappings({
            @Mapping(target = "message", source = "message"),
            @Mapping(target = "fileAttachments", ignore = true)
    })
    Message messageRequestDtoToMessage(MessageRequestDto messageRequestDto);

    @Mappings({
            @Mapping(target = "senderId", source = "sender.id"),
            @Mapping(target = "receiverId", source = "receiver.id"),
            @Mapping(target = "createdDate", dateFormat = CommonConstant.PATTERN_DATE_TIME),
            @Mapping(target = "lastModifiedDate", dateFormat = CommonConstant.PATTERN_DATE_TIME),
            @Mapping(target = "fileAttachments", expression = "java(mapFileAttachments(message.getFileAttachments()))")
    })
    MessageResponseDto messageToMessageResponseDto(Message message);

    List<MessageResponseDto> messagesToMessageResponseDtos(List<Message> messages);
//    default List<MessageResponseDto> messagesToMessageResponseDtos(List<Message> messages) {
//        List<MessageResponseDto> messageResponseDtoList = new ArrayList<>();
//        for (Message message : messages) {
//            MessageResponseDto messageResponseDto = messageToMessageResponseDto(message);
//            messageResponseDtoList.add(messageResponseDto);
//        }
//        return messageResponseDtoList;
//    }

    default List<FileAttachmentResponseDto> mapFileAttachments(List<FileAttachment> fileAttachments) {
        List<FileAttachmentResponseDto> files = new ArrayList<>();
        if (fileAttachments != null) {
            for (FileAttachment fileAttachment : fileAttachments) {
                files.add(this.fileAttachmentToFileAttachmentResponseDto(fileAttachment));
            }
        }
        return files;
    }
}
