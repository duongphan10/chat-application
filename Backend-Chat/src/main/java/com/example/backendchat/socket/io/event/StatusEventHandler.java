package com.example.backendchat.socket.io.event;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.domain.dto.response.UserDto;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.domain.mapper.UserMapper;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusEventHandler {

    private final SocketIOServer server;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @OnEvent(CommonConstant.Event.CLIENT_SEND_REQUEST_ACTIVITY)
    public void handlerEventSendRequestActivityTime(SocketIOClient client, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        UserDto userDto = userMapper.toUserDto(user);
        server.getRoomOperations(client.get(CommonConstant.Key.USER_ID))
                .sendEvent(CommonConstant.Event.SEVER_SEND_RESPONSE_ACTIVITY, userDto);
    }

}
