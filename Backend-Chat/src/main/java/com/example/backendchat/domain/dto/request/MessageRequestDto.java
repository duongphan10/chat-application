package com.example.backendchat.domain.dto.request;

import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.domain.dto.common.DateAuditingDto;
import com.example.backendchat.validator.annotation.ValidListFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MessageRequestDto extends DateAuditingDto {
    private String message;
    @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
    private String receiverId;
    @ValidListFile
    private List<MultipartFile> fileAttachments;
}
