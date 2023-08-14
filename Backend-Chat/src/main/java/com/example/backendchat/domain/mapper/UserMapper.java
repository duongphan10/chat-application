package com.example.backendchat.domain.mapper;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.domain.dto.request.UserCreateDto;
import com.example.backendchat.domain.dto.request.UserUpdateDto;
import com.example.backendchat.domain.dto.response.UserDto;
import com.example.backendchat.domain.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toUser(UserCreateDto userCreateDTO);

    @Mappings({
            @Mapping(target = "roleName", source = "role.name"),
            @Mapping(target = "activityTime", source = "activityTime", dateFormat = CommonConstant.PATTERN_DATE_TIME),
            @Mapping(target = "createdDate", source = "createdDate",dateFormat = CommonConstant.PATTERN_DATE_TIME),
            @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", dateFormat = CommonConstant.PATTERN_DATE_TIME)
    })
    UserDto toUserDto(User user);

    @Mappings({
            @Mapping(target = "user.avatar", source = "avatar", ignore = true),
            @Mapping(target = "user.fullName", source = "fullName"),
            @Mapping(target = "user.username", ignore = true),
            @Mapping(target = "user.email", ignore = true),
    })
    void updateUser(@MappingTarget User user, UserUpdateDto userUpdateDto);

    List<UserDto> toUserDtos(List<User> user);

}
