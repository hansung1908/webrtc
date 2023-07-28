package com.hansung.webrtc.Handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SignalHandler extends TextWebSocketHandler {

    List<WebSocketSession>sessions = new CopyOnWriteArrayList<>();

    // 클라이언트로부터 메시지를 받으면 보낸 사람의 세션 ID와 목록의 세션을 비교하여 보낸 사람을 제외한 다른 모든 클라이언트에 메시지를 전송
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    // 새로운 클라이언트가 생성되면 세션 목록에 추가
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        sessions.add(session);
    }
}
