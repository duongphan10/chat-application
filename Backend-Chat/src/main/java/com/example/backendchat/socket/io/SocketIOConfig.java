package com.example.backendchat.socket.io;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.example.backendchat.constant.CommonConstant;
import com.example.backendchat.constant.ErrorMessage;
import com.example.backendchat.exception.UnauthorizedException;
import com.example.backendchat.security.jwt.JwtTokenProvider;
import com.example.backendchat.socket.io.exception.SocketIOEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Value("${socket.io.host}")
    private String host;

    @Value("${socket.io.port}")
    private Integer port;

    private final SocketIOEventException socketIOEventException;

    private final JwtTokenProvider tokenProvider;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        //config.setOrigin("*");
        config.setAuthorizationListener(handshakeData -> {
            String accessToken = handshakeData.getSingleUrlParam(CommonConstant.Key.ACCESS_TOKEN);

            // Chưa xử lý case: dùng jwt để check giống http
            if (!StringUtils.hasText(accessToken) || !tokenProvider.validateToken(accessToken)) {
                throw new UnauthorizedException(ErrorMessage.FORBIDDEN);
            }
            return true;
        });
        config.setExceptionListener(socketIOEventException);
        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
