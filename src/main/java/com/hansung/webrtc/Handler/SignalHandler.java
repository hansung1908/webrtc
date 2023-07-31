package com.hansung.webrtc.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansung.webrtc.dto.Room;
import com.hansung.webrtc.dto.WebRtcMessage;
import com.hansung.webrtc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Component
public class SignalHandler extends TextWebSocketHandler {
    @Autowired
    private RoomService roomService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Room> sessionIdToRoomMap = new HashMap<>();

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessionIdToRoomMap.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {

        // 웹 소켓이 연결되었을 때 클라이언트 쪽으로 메시지를 발송한다
        // sessionIdToRoomMap.isEmpty() 가 false를 전달하면 room 에 아무도 없다는 것을 의미하고 따라서 추가적인 ICE 요청을 하지 않도록 한다.
        // true 상태가 되면 이후에 들어온 유저가 방안에 또 다른 유저가 있음을 확인하고 P2P 연결을 시작한다.

        sendMessage(session, new WebRtcMessage("Server", "join", Boolean.toString(!sessionIdToRoomMap.isEmpty()), null, null));
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) {
        // 메세지 받을 때 실행
        try {
            // 웹 소켓으로부터 전달받은 메시지
            // 소켓쪽에서는 socket.send 로 메시지를 발송한다 => 참고로 JSON 형식으로 변환해서 전달해온다
            WebRtcMessage message = objectMapper.readValue(textMessage.getPayload(), WebRtcMessage.class);
            String userName = message.getFrom(); // 유저 uuid
            String data = message.getData(); // roomId

            Room room;

            // 메시지 타입에 따라서 서버에서 하는 역할이 달라진다
            switch (message.getType()) {

                // 클라이언트에게서 받은 메시지 타입에 따른 signal 프로세스
                case "offer", "answer", "ice" -> {
                    Object candidate = message.getCandidate();
                    Object sdp = message.getSdp();
                    Room rm = sessionIdToRoomMap.get(session.getId());
                    if (rm != null) {
                        Map<String, WebSocketSession> clients = roomService.getClients(rm);
                        for (Map.Entry<String, WebSocketSession> client : clients.entrySet()) {
                            // 현재 사용자를 제외한 모든 클라이언트에 메시지 보내기
                            if (!client.getKey().equals(userName)) {
                                // 신호를 다시 보낼 동일한 유형 선택
                                sendMessage(client.getValue(),
                                        new WebRtcMessage(
                                                userName,
                                                message.getType(),
                                                data,
                                                candidate,
                                                sdp));
                            }
                        }
                    }
                }

                // 사용자와 상대를 식별
                case "join" -> {
                    // message.data에 연결된 채팅방 id 포함
                    room = roomService.findRoomByStringId(data)
                            .orElseThrow(() -> new IOException("Invalid room number received!"));
                    // 채팅방 유저 목록에 클라이언트 추가
                    roomService.addClient(room, userName, session);
                    sessionIdToRoomMap.put(session.getId(), room);
                }

                case "leave" -> {
                    // message.data에 연결된 채팅방 id 포함
                    // session id로 지정된 채팅방
                    room = sessionIdToRoomMap.get(session.getId());
                    // 채팅방 유저 목록에서 나간 클라이너트 제거
                    Optional<String> client = roomService.getClients(room).entrySet().stream()
                            .filter(entry -> Objects.equals(entry.getValue().getId(), session.getId()))
                            .map(Map.Entry::getKey)
                            .findAny();
                    client.ifPresent(c -> roomService.removeClientByName(room, c));
                }

                // 이외의 메세지 유형은 인식할 수 없음
                default -> System.out.println("messageType error");
            }

        } catch (IOException e) {
            System.out.println("handleTextMessage error : " + e.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, WebRtcMessage message) {

        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            System.out.println("sendMessage error : " + e.getMessage());
        }
    }
}
