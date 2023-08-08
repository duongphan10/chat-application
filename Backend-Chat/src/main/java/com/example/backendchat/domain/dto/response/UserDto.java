package com.example.backendchat.domain.dto.response;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.domain.dto.common.DateAuditingDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String id;
    private String fullName;
    private String username;
    private String email;
    private String avatar;
    private String status;
    private String activityTime;
    private String roleName;
    private String createdDate;
    private String lastModifiedDate;
}

