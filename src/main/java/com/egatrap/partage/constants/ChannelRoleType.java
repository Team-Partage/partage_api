package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelRoleType {

    ROLE_OWNER("C0000", 0),
    ROLE_MODERATOR("C0100", 1),
    ROLE_VIEWER("C0200", 2),
    ROLE_NONE("C0300", 3);

    private final String ROLE_ID;
    private final int ROLE_PRIORITY;

    public static ChannelRoleType get(String roleId) {
        for (ChannelRoleType roleType : ChannelRoleType.values()) {
            if (roleType.ROLE_ID.equals(roleId)) {
                return roleType;
            }
        }
        return null;
    }


}
