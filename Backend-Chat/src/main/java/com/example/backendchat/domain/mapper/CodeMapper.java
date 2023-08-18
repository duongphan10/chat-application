package com.example.backendchat.domain.mapper;

import com.example.backendchat.domain.dto.response.CodeResponseDto;
import com.example.backendchat.domain.entity.Code;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CodeMapper {
    @Mappings({
            @Mapping(target = "expirationTime", source = "expirationTime"),
            @Mapping(target = "email", source = "user.email"),
            @Mapping(target = "userId", source = "user.id"),
    })
    CodeResponseDto codeToCodeResponseDto(Code code);
}
