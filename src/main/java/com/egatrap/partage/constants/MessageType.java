package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    CHANNEL_INFO(1),
    CHANNEL_VIEWER(2),
    USER_INFO(3),
    VIDEO_CURRENT(4),

    USER_CHAT(11),
    USER_JOIN(12),
    USER_LEAVE(13),
    USER_BAN(14),

    PLAYLIST_ADD(21),
    PLAYLIST_REMOVE(22),
    PLAYLIST_MOVE(23),

    VIDEO_PLAY(31),
    VIDEO_SEEK(32),
    VIDEO_SKIP(33),

    NONE(0);

    private final long id;
}
