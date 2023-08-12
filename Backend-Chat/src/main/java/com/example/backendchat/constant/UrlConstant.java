package com.example.backendchat.constant;

public class UrlConstant {

    public static class Auth {
        private static final String PRE_FIX = "/auth";

        public static final String LOGIN = PRE_FIX + "/login";
        public static final String LOGOUT = PRE_FIX + "/logout";
        public static final String REGISTER = PRE_FIX + "/register";
        public static final String RESET_PASSWORD = PRE_FIX + "/reset-password";

        private Auth() {
        }
    }

    public static class User {
        private static final String PRE_FIX = "/user";

        public static final String GET_USERS = PRE_FIX;
        public static final String GET_USER = PRE_FIX + "/{userId}";
        public static final String GET_CURRENT_USER = PRE_FIX + "/current";
        public static final String UPDATE_USER = PRE_FIX;
        public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";

        public static final String GET_FOLLOWERS = PRE_FIX + "/followers";
        public static final String GET_FOLLOWING = PRE_FIX + "/following";
        public static final String GET_ALL_USER_CONVERSATION = PRE_FIX + "/all/conversation";
        public static final String SEARCH_FRIEND = PRE_FIX + "/search/friend";
        public static final String SEARCH_OTHER_USER = PRE_FIX + "/search/other";
        public static final String GET_USER_BY_USERNAME = PRE_FIX + "/get";
        private User() {
        }
    }

    public static class Message {
        private static final String PRE_FIX = "/message";

        public static final String SEND_MESSAGE_TO_OTHER = PRE_FIX;
        public static final String GET_MESSAGES_BY_OTHER_BY_ID = PRE_FIX + "/me/{receiverId}";

        private Message() {
        }
    }

}
