package com.example.backendchat.domain.dto.request;

import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.validator.annotation.ValidFileImage;
import com.example.backendchat.validator.annotation.ValidUsername;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDto {
//  @NotBlank(message = ErrorMessage.NOT_BLANK_FIELD)
  private String fullName;
  @ValidUsername
  private String username;
  @Email(regexp = "^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$",
          message = ErrorMessage.INVALID_FORMAT_EMAIL)
  private String email;
  @ValidFileImage
  private MultipartFile avatar;

}
