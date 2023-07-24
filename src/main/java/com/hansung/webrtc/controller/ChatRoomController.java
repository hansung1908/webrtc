package com.hansung.webrtc.controller;

import com.hansung.webrtc.dto.ChatRoomDto;
import com.hansung.webrtc.dto.ChatRoomMap;
import com.hansung.webrtc.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private ChatService chatService;

    // 채팅방 생성
    @PostMapping("/chat/createroom")
    public String createRoom(@RequestParam("roomName") String name,
                             @RequestParam("roomPwd") String roomPwd,
                             @RequestParam("secretChk") String secretChk,
                             @RequestParam(value = "maxUserCnt", defaultValue = "2") String maxUserCnt,
                             RedirectAttributes rttr) {

        ChatRoomDto room = chatService.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretChk), Integer.parseInt(maxUserCnt));

        rttr.addFlashAttribute("roomName", room);
        return "redirect:/";
    }

    // 채팅방 입장 화면
    @GetMapping
    public String roomDetail(Model model, String roomId) {

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);

        model.addAttribute("room", room);
        model.addAttribute("uuid", UUID.randomUUID().toString());

        return "rtcroom";
    }

    // 채팅방 비밀번호 확인
    @PostMapping("/chat/confirmPwd/{roomId}")
    @ResponseBody
    public boolean confirmPwd(@PathVariable String roomId, @RequestParam String roomPwd) {

        // 넘어온 roomId 와 roomPwd 를 이용해서 비밀번호 찾기
        // 찾아서 입력받은 roomPwd 와 room pwd 와 비교해서 맞으면 true, 아니면  false
        return chatService.confirmPwd(roomId, roomPwd);
    }

    // 채팅방 삭제
    @GetMapping("/chat/delRoom/{roomId}")
    public String deleteChatRoom(@PathVariable String roomId) {

        // roomId 기준으로 chatRoomMap 에서 삭제
        chatService.deleteChatRoom(roomId);

        return "redirect:/";
    }

    // 유저 카운트
    @GetMapping("/chat/chkUserCnt/{roomId}")
    @ResponseBody
    public boolean checkRoomUserCount(@PathVariable String roomId){

        return chatService.checkRoomUserCount(roomId);
    }
}
