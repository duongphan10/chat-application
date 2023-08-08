package com.example.backendchat.util;

import java.util.Random;

public class RandomString {

    public static String generate(int length) {
        String characters = "ABCDEFGHIJK0123456789LMNOPQRSTUVWXYZ0123456789abcdefghijklm0123456789nopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

}
