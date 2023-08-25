package com.example.backendchat.validator;

import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.validator.annotation.ValidListFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ListFileValidator implements ConstraintValidator<ValidListFile, List<MultipartFile>> {
    private String fileTypeNotAllowedMessage;
    private String fileSizeExceededMessage;

    @Override
    public void initialize(ValidListFile constraintAnnotation) {
        this.fileTypeNotAllowedMessage = constraintAnnotation.fileTypeNotAllowedMessage();
        this.fileSizeExceededMessage = constraintAnnotation.fileSizeExceededMessage();
    }

    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return true;
        }
        int imageFile = 0;
        int videoFile = 0;
        for (MultipartFile file : multipartFiles) {
            String contentType = file.getContentType();
            if (contentType == null || !isSupportedContentType(contentType)) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(fileTypeNotAllowedMessage)
                        .addConstraintViolation();
                return false;
            }
            if ((contentType.startsWith("image/") && file.getSize() > CommonConstant.MAX_IMAGE_SIZE_BYTES) ||
                    file.getSize() > CommonConstant.MAX_VIDEO_SIZE_BYTES) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(fileSizeExceededMessage)
                        .addConstraintViolation();
                return false;
            }
            if (contentType.startsWith("image/"))
                imageFile++;
            else
                videoFile++;
        }
        return (imageFile <= CommonConstant.MAX_NUMBER_IMAGE && videoFile <= CommonConstant.MAX_NUMBER_VIDEO);
    }

    private boolean isSupportedContentType(String contentType) {
        if (contentType.startsWith("image/")) {
            return CommonConstant.CONTENT_TYPE_IMAGE.contains(contentType.substring("image/".length()));
        } else if (contentType.startsWith("video/")) {
            return CommonConstant.CONTENT_TYPE_VIDEO.contains(contentType.substring("video/".length()));
//        } else if (contentType.startsWith("audio/")) {
//            return CommonConstant.CONTENT_TYPE_AUDIO.contains(contentType.substring("audio/".length()));
//        } else if (contentType.startsWith("application/")) {
//            return CommonConstant.CONTENT_TYPE_DOCUMENT.contains(contentType.substring("application/".length()));
//        } else if (contentType.startsWith("text/")) {
//            return CommonConstant.CONTENT_TYPE_DOCUMENT_TEXT.contains(contentType.substring("text/".length()));
        }
        return false;
    }

}
