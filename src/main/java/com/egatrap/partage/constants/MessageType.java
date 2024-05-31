package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    USER_CHAT,
    USER_JOIN,
    USER_LEAVE,
    USER_BAN,

    PLAYLIST_ADD,
    PLAYLIST_REMOVE,
    PLAYLIST_MOVE,

    VIDEO_PLAY,
    VIDEO_SEEK,
    VIDEO_SKIP,

    NONE

}
