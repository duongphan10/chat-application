package com.example.backendchat.validator.annotation;


import com.example.backendchat.validator.ListFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = {ListFileValidator.class})
public @interface ValidListFile {

    String message() default "invalid.number.of.files";

    String fileTypeNotAllowedMessage() default "invalid.file.type";

    String fileSizeExceededMessage() default "invalid.file.size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
