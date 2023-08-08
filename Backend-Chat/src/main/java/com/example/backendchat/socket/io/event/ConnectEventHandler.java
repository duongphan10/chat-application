package com.example.backendchat.socket.io.event;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.constant.StatusConstant;
import com.example.backendchat.domain.entity.User;
import com.example.backendchat.exception.NotFoundException;
import com.example.backendchat.repository.UserRepository;
import com.example.backendchat.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectEventHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    @OnConnect
    private void onConnect(SocketIOClient client) {
        HandshakeData handshakeData = client.getHandshakeData();
        String accessToken = handshakeData.getSingleUrlParam(CommonConstant.Key.ACCESS_TOKEN);
        client.set(CommonConstant.Key.ACCESS_TOKEN, accessToken);

        String userId = tokenProvider.extractSubjectFromJwt(accessToken);
        client.set(CommonConstant.Key.USER_ID, userId);
        client.joinRoom(userId);
        log.info("Client user with id {} connected!", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, new String[]{userId}));
        user.setStatus(StatusConstant.ONLINE);
        user.setActivityTime(LocalDateTime.now());
        userRepository.save(user);
    }

}
