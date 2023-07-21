package com.hansung.webrtc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ChatRoomMap {
    private static ChatRoomMap chatRoomMap = new ChatRoomMap();
    private Map<String, ChatRoomDto> chatRooms = new LinkedHashMap<>();

    public static ChatRoomMap getInstance(){
        return chatRoomMap;
    }
}
