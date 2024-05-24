package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ChannelPermissionType {

    VIEWER_ADD("C0101"),
    VIEWER_REMOVE("C0102"),
    VIEWER_MOVE("C0103"),
    VIEWER_PLAYANDPAUSE("C0104"),
    VIEWER_SEEK("C0105"),
    VIEWER_SKIP("C0106"),
    VIEWER_CHATSEND("C0107"),
    VIEWER_CHATDELETE("C0108"),
    VIEWER_BAN("C0109"),
    MODERATOR_ADD("C0201"),
    MODERATOR_REMOVE("C0202"),
    MODERATOR_MOVE("C0203"),
    MODERATOR_PLAYANDPAUSE("C0204"),
    MODERATOR_SEEK("C0205"),
    MODERATOR_SKIP("C0206"),
    MODERATOR_CHATSEND("C0207"),
    MODERATOR_CHATDELETE("C0208"),
    MODERATOR_BAN("C0209"),
    OWNER_ADD("C0301"),
    OWNER_REMOVE("C0302"),
    OWNER_MOVE("C0303"),
    OWNER_PLAYANDPAUSE("C0304"),
    OWNER_SEEK("C0305"),
    OWNER_SKIP("C0306"),
    OWNER_CHATSEND("C0307"),
    OWNER_CHATDELETE("C0308"),
    OWNER_BAN("C0309");

    private final String PERMISSION_ID;

    public static ChannelPermissionType of(String id) {
        for (ChannelPermissionType channelRoleType : values()) {
            if (channelRoleType.PERMISSION_ID.equals(id)) {
                return channelRoleType;
            }
        }
        throw new IllegalArgumentException("Unknown user role id: " + id);
    }


    public static List<ChannelPermissionType> defaultChannelPermissionList() {

        ChannelPermissionType[] channelPermissionTypes = {
                ChannelPermissionType.VIEWER_ADD,
                ChannelPermissionType.VIEWER_CHATSEND,
                ChannelPermissionType.MODERATOR_ADD,
                ChannelPermissionType.MODERATOR_REMOVE,
                ChannelPermissionType.MODERATOR_MOVE,
                ChannelPermissionType.MODERATOR_PLAYANDPAUSE,
                ChannelPermissionType.MODERATOR_SEEK,
                ChannelPermissionType.MODERATOR_SKIP,
                ChannelPermissionType.MODERATOR_CHATSEND,
                ChannelPermissionType.OWNER_ADD,
                ChannelPermissionType.OWNER_REMOVE,
                ChannelPermissionType.OWNER_MOVE,
                ChannelPermissionType.OWNER_PLAYANDPAUSE,
                ChannelPermissionType.OWNER_SEEK,
                ChannelPermissionType.OWNER_SKIP,
                ChannelPermissionType.OWNER_CHATSEND,
                ChannelPermissionType.OWNER_CHATDELETE,
                ChannelPermissionType.OWNER_BAN
        };

        return new ArrayList<>(List.of(channelPermissionTypes));
    }
}