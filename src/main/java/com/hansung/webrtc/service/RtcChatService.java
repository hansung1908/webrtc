package com.hansung.webrtc.service;

import com.hansung.webrtc.dto.ChatRoomDto;
import com.hansung.webrtc.dto.ChatRoomMap;
import com.hansung.webrtc.model.WebRtcMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RtcChatService {

    public ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {

        // roomName 와 roomPwd 로 chatRoom 빌드 후 return
        ChatRoomDto room = ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .roomPwd(roomPwd)
                .secretChk(secretChk)
                .userCount(0)
                .maxUserCnt(maxUserCnt)
                .build();

        room.setUserList(new HashMap<String, WebSocketSession>());

        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);

        return room;
    }

    // 클라이언트 호출
    public Map<String, WebSocketSession> getClients(ChatRoomDto room) {

        Optional<ChatRoomDto> roomDto = Optional.ofNullable(room);

        return (Map<String, WebSocketSession>) roomDto.get().getUserList();
    }

    // 클라이언트 추가
    public Map<String, WebSocketSession> addClient(ChatRoomDto room, String name, WebSocketSession session) {

        Map<String, WebSocketSession> userList = (Map<String, WebSocketSession>) room.getUserList();
        userList.put(name, session);

        return userList;
    }

    // 클라이언트 삭제
    public void removeClientByName(ChatRoomDto room, String userUUID) {

        room.getUserList().remove(userUUID);
    }

    // 유저 카운터 반환
    public boolean findUserCount(WebRtcMessage webRtcMessage) {

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(webRtcMessage.getData());

        return room.getUserList().size() > 1;
    }
}
