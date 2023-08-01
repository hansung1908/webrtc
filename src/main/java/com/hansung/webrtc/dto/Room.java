package com.hansung.webrtc.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

// WebRTC 연결 시 채팅방 데이터 담는 클래스
@Data
public class Room {

    public Room(Long id) {
        this.id = id;
    }

    @NotNull
    private Long id;
    private Map<String, WebSocketSession> clients = new HashMap<>();
}
