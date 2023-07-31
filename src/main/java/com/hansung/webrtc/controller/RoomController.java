package com.hansung.webrtc.controller;

import com.hansung.webrtc.dto.Room;
import com.hansung.webrtc.service.RoomService;
import com.hansung.webrtc.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class RoomController {

    @Autowired
    private Parser parser;

    @Autowired
    private RoomService roomService;

    @GetMapping({"", "/", "/index", "/home", "/main"})
    public ModelAndView displayMainPage(Long id, String uuid) {

        ModelAndView modelAndView = new ModelAndView("main");

        modelAndView.addObject("id", id);
        modelAndView.addObject("rooms", roomService.getRooms());
        modelAndView.addObject("uuid", uuid);

        return modelAndView;
    }

    @PostMapping(value = "/room", params = "action=create")
    public ModelAndView processRoomSelection(@ModelAttribute("id") String sid, @ModelAttribute("uuid") String uuid) {

        Optional<Long> optionalId = parser.parseId(sid);
        optionalId.ifPresent(id -> Optional.ofNullable(uuid).ifPresent(name -> roomService.addRoom(new Room(id))));

        return this.displayMainPage(optionalId.orElse(null), uuid);
    }

    @GetMapping("/room/{sid}/user/{uuid}")
    public ModelAndView displaySelectedRoom(@PathVariable("sid") String sid, @PathVariable("uuid") String uuid) {

        // 데이터가 유효하지 않을 경우 리다이렉트
        ModelAndView modelAndView = new ModelAndView("redirect:/");

        if (parser.parseId(sid).isPresent()) {
            Room room = roomService.findRoomByStringId(sid).orElse(null);
            if(room != null && uuid != null && !uuid.isEmpty()) {
                // 채팅방 열기
                modelAndView = new ModelAndView("chat_room", "id", sid);
                modelAndView.addObject("uuid", uuid);
            }
        }

        return modelAndView;
    }

    @GetMapping("/room/{sid}/user/{uuid}/exit")
    public ModelAndView processRoomExit(@PathVariable("sid") String sid, @PathVariable("uuid") String uuid) {
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/room/random")
    public ModelAndView requestRandomRoomNumber(@ModelAttribute("uuid") String uuid) {
        return this.displayMainPage(ThreadLocalRandom.current().nextLong(0, 100), uuid);
    }

}
