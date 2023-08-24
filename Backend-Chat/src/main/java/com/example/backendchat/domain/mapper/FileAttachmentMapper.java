package com.example.backendchat.domain.mapper;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.domain.dto.response.FileAttachmentResponseDto;
import com.example.backendchat.domain.entity.FileAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileAttachmentMapper {
    @Mappings({
            @Mapping(target = "messageId", source = "message.id"),
            @Mapping(target = "createdDate", dateFormat = CommonConstant.PATTERN_DATE_TIME),
            @Mapping(target = "lastModifiedDate", dateFormat = CommonConstant.PATTERN_DATE_TIME),
    })
    FileAttachmentResponseDto fileAttachmentToFileAttachmentResponseDto(FileAttachment fileAttachment);
}
