package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    CHANNEL_INFO(10), // 채널 정보
    CHANNEL_VIEWER(11), // 채널 시청자 수
    VIDEO_TIME(12), // 현재 재생중인 비디오 시간

    USER_CHAT(20), // 채팅 메시지
    USER_JOIN(21), // 사용자 입장 알림
    USER_LEAVE(22), // 사용자 퇴장 알림

    PLAYLIST_SELECT(30), // 플레이리스트 선택
    PLAYLIST_ADD(31), // 플레이리스트 추가
    PLAYLIST_REMOVE(32), // 플레이리스트 삭제
    PLAYLIST_MOVE(33), // 플레이리스트 이동
    VIDEO_PLAY(34), // 비디오 재생 및 일시정지
    VIDEO_MOVE(35), // 비디오 이동 (시간)

    CHANNEL_USER_ROLE_CHANGE(40),  // 채널 사용자 권한 변경
    CHANNEL_PERMISSION_CHANGE(41), // 채널 permission 변경

    ERROR_SERVER(90), // 서버 오류
    ERROR_CLIENT(91), // 클라이언트 오류

    NONE(0); // 기본

    private final long id;
}
