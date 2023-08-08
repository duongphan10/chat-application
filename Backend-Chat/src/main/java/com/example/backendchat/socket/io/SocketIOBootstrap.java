package com.example.backendchat.socket.io;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketIOBootstrap {

    private final SocketIOServer server;

    @PostConstruct
    public void startSocketIOServer() {
        log.info("--------------starting socket.io server--------------");
        server.start();
    }

    @PreDestroy
    public void stopSocketIOServer() {
        log.info("--------------stop socket.io server--------------");
        server.stop();
    }

}