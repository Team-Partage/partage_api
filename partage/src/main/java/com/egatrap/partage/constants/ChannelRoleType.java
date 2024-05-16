package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelRoleType {

    READ("R0000"),
    WRITE("R0100"),
    OWNER("R0200");

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
