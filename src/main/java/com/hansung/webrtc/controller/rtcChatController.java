package com.hansung.webrtc.controller;

import com.hansung.webrtc.model.WebRtcMessage;
import com.hansung.webrtc.service.RtcChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class rtcChatController {

    private final RtcChatService rtcChatService;

    @PostMapping("/webrtc/usercount")
    public String webRTC(@ModelAttribute WebRtcMessage webRtcMessage) {

        return Boolean.toString(rtcChatService.findUserCount(webRtcMessage));
    }
}
