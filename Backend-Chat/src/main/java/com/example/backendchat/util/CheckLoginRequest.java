package com.example.backendchat.util;

import java.util.regex.Pattern;

public class CheckLoginRequest {

    private static final Pattern emailPattern = Pattern.compile("^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$");
    private static final Pattern phonePattern = Pattern.compile("^(0?)(3[2-9]|5[2|6|8|9]|7[0|6-9]|8[0-9]|9[0-4|6-9])[0-9]{7}$");

    public static Boolean isEmail(String email) {
        return emailPattern.matcher(email).matches();
    }

    public static Boolean isPhone(String phone) {
        return phonePattern.matcher(phone).matches();
    }

}
