package com.hansung.webrtc.config;

import com.hansung.webrtc.handler.SignalHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

// 시그널링 서버 - 클라이언트 어플리케이션이 websocket 연결을 등록하는데 사용할 수 있는 엔드 포인트

@Configuration
@EnableWebSocket
public class WebRtcConfig implements WebSocketConfigurer {

    private SignalHandler signalHandler;

    // /signal로 요청이 오면 registerWebSocketHandlers가 동작하도록 registry를 설정
    // 1. 메세지를 처리할 처리기, 2. 식별할 url, 3. 모든 출처에서 오는 요청을 허용
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal").setAllowedOrigins("*");
    }

    // 웹 소켓에서 rtc 통신을 위한 최대 텍스트 버퍼와 바이너리 버퍼 사이즈를 설정
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean containerFactoryBean = new ServletServerContainerFactoryBean();
        containerFactoryBean.setMaxTextMessageBufferSize(8192);
        containerFactoryBean.setMaxBinaryMessageBufferSize(8192);
        return containerFactoryBean;
    }
}
