package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelRoleType {

    ROLE_VIEWER("C0100"),
    ROLE_MODERATOR("C0200"),
    ROLE_OWNER("C0300");

    private final String ROLE_ID;

    public static ChannelRoleType of(String id) {
        for (ChannelRoleType channelRoleType : values()) {
            if (channelRoleType.ROLE_ID.equals(id)) {
                return channelRoleType;
            }
        }
        throw new IllegalArgumentException("Unknown user role id: " + id);
    }
}
