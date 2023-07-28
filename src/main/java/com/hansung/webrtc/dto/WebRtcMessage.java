package com.hansung.webrtc.dto;

import lombok.Data;

// WebRTC 연결 시 메세지 내용을 담는 클래스
@Data
public class WebRtcMessage {
    private String from; // 보내는 유저
    private String type; // 메세지 타입
    private String data; // room id
    private Object candidate; // 상태
    private Object sdp; // sdp 정보
}
