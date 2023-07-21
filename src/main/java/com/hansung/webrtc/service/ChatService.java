package com.hansung.webrtc.service;

import com.hansung.webrtc.dto.ChatRoomDto;
import com.hansung.webrtc.dto.ChatRoomMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class ChatService {

    private RtcChatService rtcChatService;

    // 전체 채팅방 조회
    public List<ChatRoomDto> findAllRoom() {

        // 채팅방 생성 순서를 최근 순서로 반환
        List<ChatRoomDto> chatRooms = new ArrayList<>(ChatRoomMap.getInstance().getChatRooms().values());
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    // roomId 기준으로 채팅방 찾기
    public ChatRoomDto findByRoomId(String roomId) {

        return ChatRoomMap.getInstance().getChatRooms().get(roomId);
    }

    //
    public ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {

        ChatRoomDto room = rtcChatService.createChatRoom(roomName, roomPwd, secretChk, maxUserCnt);
        return room;
    }

    // 채팅방 비밀번호 조회
    public boolean confirmPwd(String roomId, String roomPwd) {

        return roomPwd.equals(ChatRoomMap.getInstance().getChatRooms().get(roomId).getRoomPwd());
    }

    // 채팅방 인원 + 1
    public void plusUserCount(String roomId) {

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        room.setUserCount(room.getUserCount() + 1);
    }

    // 채팅방 인원 - 1
    public void minusUserCount(String roomId) {

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        room.setUserCount(room.getUserCount() - 1);
    }

    // 최대 채팅방 유저 수에 따른 입장 여부
    public boolean checkRoomUserCount(String roomId) {

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);

        if(room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }

        return true;
    }

    // 채팅방 삭제
    public void deleteChatRoom(String roomId) {
        ChatRoomMap.getInstance().getChatRooms().remove(roomId);
    }
}
