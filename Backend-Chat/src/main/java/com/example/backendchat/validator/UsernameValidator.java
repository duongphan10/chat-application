package com.example.backendchat.validator;

import com.example.backendchat.validator.annotation.ValidUsername;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private Pattern pattern;

    @Override
    public void initialize(ValidUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        String regex = constraintAnnotation.regexp();
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (username == null) return true;
        return pattern.matcher(username).matches();
    }
}
