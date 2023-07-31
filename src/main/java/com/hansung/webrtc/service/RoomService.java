package com.hansung.webrtc.service;

import com.hansung.webrtc.dto.Room;
import com.hansung.webrtc.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
public class RoomService {

    @Autowired
    private Parser parser;

    // comparator는 room에 담겨있는 방의 번호를 바탕으로 정렬
    private final Set<Room> rooms = new TreeSet<>(Comparator.comparing(Room::getId));

    public Set<Room> getRooms() {
        TreeSet<Room> copy = new TreeSet<>(Comparator.comparing(Room::getId));
        copy.addAll(rooms);
        return copy;
    }

    public Optional<Room> findRoomByStringId(String sid) {
        return rooms.stream().filter(r -> r.getId().equals(parser.parseId(sid).get())).findAny();
    }

    public Boolean addRoom(Room room) {
        return rooms.add(room);
    }

    public Long getRoomId(Room room) {
        return room.getId();
    }

    public Map<String, WebSocketSession> getClients(Room room) {

        // unmodifiableMap : read-only 객체를 만들고 싶을 때 사용
        // Collections.emptyMap() : 결과를 반환할 시 반환할 데이터가 없거나 내부조직에 의해 빈 데이터가 반환되어야 하는 경우
        // NullPointException 을 방지하기 위하여 반환 형태에 따라 List 나 Map 의 인스턴스를 생성하여 반환하여 처리해야하는 경우
        // size 메서드 등을 체크하고 추가적인 값을 변경하지 않는 경우 Collections.emptyMap() 를 사용하면 매번 동일한 정적 인스턴스가
        // 변환되므로 각 호출에 대한 불필요한 인스턴스 생성하지 않게 되어 메모리 사용량을 줄일 수 있다

        return Optional.ofNullable(room)
                .map(r -> Collections.unmodifiableMap(r.getClients()))
                .orElse(Collections.emptyMap());
    }

    public WebSocketSession addClient(Room room, String name, WebSocketSession session) {
        return room.getClients().put(name, session);
    }

    public WebSocketSession removeClientByName(Room room, String name) {
        return room.getClients().remove(name);
    }
}
