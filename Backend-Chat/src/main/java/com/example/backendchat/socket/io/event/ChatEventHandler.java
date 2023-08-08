package com.example.backendchat.socket.io.event;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.domain.dto.request.MessageRequestDto;
import com.example.backendchat.domain.dto.response.MessageResponseDto;
import com.example.backendchat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEventHandler {

    private final SocketIOServer server;

    private final MessageService messageService;

    @OnEvent(CommonConstant.Event.CLIENT_SEND_MESSAGE)
    public void handlerEventSendMessage(SocketIOClient client, MessageRequestDto message) {
        String senderId = client.get(CommonConstant.Key.USER_ID);
        log.info("User {} send message: {}", senderId, message.getMessage());

        MessageResponseDto messageResponseDto = messageService
                .sendMessage(senderId, message);

        server.getRoomOperations(senderId)
                .sendEvent(CommonConstant.Event.SERVER_SEND_MESSAGE, messageResponseDto);
        server.getRoomOperations(message.getReceiverId())
                .sendEvent(CommonConstant.Event.SERVER_SEND_MESSAGE, messageResponseDto);
    }

}
