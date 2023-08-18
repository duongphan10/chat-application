package com.example.backendchat.domain.dto.response;

import com.example.backendchat.domain.dto.common.DateAuditingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CodeResponseDto extends DateAuditingDto {
    private String id;
    private String verificationCode;
    private LocalDateTime expirationTime;
    private String email;
    private String userId;
}
