package com.hansung.webrtc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;
import java.util.logging.SocketHandler;

// 시그널링 서버 - 클라이언트 어플리케이션이 websocket 연결을 등록하는데 사용할 수 있는 엔드 포인트

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // 1. 메세지를 처리할 처리기, 2. 식별할 url, 3. 모든 출처에서 오는 요청을 허용
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        try {
            registry.addHandler((WebSocketHandler) new SocketHandler(), "/socket").setAllowedOrigins("*");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
