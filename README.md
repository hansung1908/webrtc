# webrtc
- web real-time communication의 약자로 웹 또는 에서 별도의 소프트웨어, 플러그인 없이 음성, 영상, 텍스트 같은 데이터를 브라우저끼리 주고 받을 수 있게 해주는 기술

### 실시간 통신을 위해 webrtc에서 제공하는 api
- mediastream: 카메라와 마이크 등의 데이터 스트림 접근
- rtcpeerconnection: 암호화 및 대역폭 관리 및 오디오, 비디오 연결
- rtcdatachannel: 일반적인 데이터의 p2p 통신

### 실시간 통신을 위해 구현해야 하는 서버
- signaling server: peer끼리 연결하기 위해서는 Session Control Messages, Error Messages, Codec, Bandwith 등의 다양한 정보를 각각의 peer들에게 전달되어야 하며 이 프로세스를 signaling이라고 하며 이러한 정보들을 중개해주는 역할의 서버
- stun server(session traversal utilities for nat): 통신 중간에 방화벽, NAT 환경에 놓여 있는 Peer에 대해서는 직접적인 Signaling이 불가능하므로 클라이언 자신의 공인 ip를 알려주는 서버
- turn server(traversal using relays around nat): stun server로 해결을 못하여 Local IP와 Public IP 둘다로도 연결할 수 없는 경우 최후의 수단으로 사용하는 서버

### media server
- 각각의 Peer들은 Media Server에게 미디어 스트림들을 쏴주고 Media Server에서 미디어 트래픽을 관리하여 각각의 Peer에게 다시 배포해주는 멀티미디어 미들웨어
- Mesh 방식
  - 특징
  - 앞서 설명한 Signaling Server, STUN Server, TURN Server를 사용하는 전형적인 P2P WebRTC 구현 방식
  - 1:1 연결 혹은 소규모 연결에 적합

  - 장점
  - Peer간의 Signaling 과정만 서버가 중계하기 때문에 서버의 부하가 감소
  - 직접적으로 Peer간 연결되기 때문에 실시간 성이 보장

  - 단점
  - 연결된 Client의 수가 늘어날 수록 Client의 과부하가 급격하게 증가
  - N명이 접속한 화상회의라면, 클라이언트 각각에서 N-1개의 연결을 유지하므로 과부하 증가

- MCU(Multi-point Control Unit) 방식
  - 특징
  - 다수의 송출 미디어 데이터를 Media Server에서 혼합(muxing) 또는 가공(transcoding)하여 수신측으로 전달하는 방식
  - P2P 방식 X, Server와 Client 간의 peer를 연결
  - Media Server의 매우 높은 컴퓨팅 파워가 요구

  - 장점
  - Client의 부하가 크게 감소
  - N:M 구조에서 사용 가능

  - 단점
  - 실시간 성이 저해
  - 구현 난이도가 상당히 어려우며 비디오와 오디오를 혼합 및 가공하는 과정에서 고난도 기술과 서버의 큰 자원이 필요

- SFU(Selective Forwarding Unit) 방식
  - 특징
  - 각각의 Client 간 미디어 트래픽을 중계하는 Media Server를 두는 방식
  - P2P 방식 X, Server와 Client 간의 peer를 연결
  - Server에게 자신의 영상 데이터를 보내고, 상대방의 수만큼 데이터를 받는 형식
  - 1:N 혹은 소규모 N:M 형식의 실시간 스트리밍에 적합

  - 장점
  - Mesh 방식보다는 느림, 하지만 비슷한 수준의 실시간성을 유지 가능
  - Mesh 방식보다는 Client의 부하가 감소

  - 단점
  - Mesh 방식보다는 서버의 부하가 증가
  - 대규모 N:M 구조에서는 여전히 Client의 부하 증가

### https 설정
- webrtc를 통해 웹에서 캠을 이용할 때 http로는 접속이 안되어 https 포트로 따로 설정해야 함
- cmd에서 keytool -genkey -alias [인증서이름] -keyalg RSA -keysize 2048 -validity 700 -keypass [인증서패스워드] -storepass [저장소패스워드] -keystore [인증서파일명].jks (https 인증서를 발급)
- application.properties에 인증서에 대한 정보를 설정하고 https 포트 또한 http 포트와 구별하여 설정
- sslconfig 파일은 http로 요청이 들어오면 https포트로 리다이렉션
