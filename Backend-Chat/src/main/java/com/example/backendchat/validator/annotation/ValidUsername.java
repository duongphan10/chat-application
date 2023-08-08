package com.example.backendchat.validator.annotation;

import com.example.backendchat.validator.UsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Constraint(validatedBy = {UsernameValidator.class})
public @interface ValidUsername {

    String message() default "invalid.username-format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regexp() default "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$";
}
