package com.example.backendchat.socket.io.event;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.constant.StatusConstant;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DisconnectEventHandler {
    private final UserRepository userRepository;
    @OnDisconnect
    private void onDisconnect(SocketIOClient client) {
        for (String room : client.getAllRooms()) {
            client.leaveRoom(room);
        }
        String userId = client.get(CommonConstant.Key.USER_ID);
        log.info("Client user with id {} disconnected", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        user.setStatus(StatusConstant.OFFLINE);
        user.setActivityTime(LocalDateTime.now());
        userRepository.save(user);
    }

}