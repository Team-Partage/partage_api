package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelRoleType {

    ROLE_VIEWER("C0100", 2),
    ROLE_MODERATOR("C0200", 1),
    ROLE_OWNER("C0300", 0);

    private final String ROLE_ID;
    private final int ROLE_PRIORITY;

    public static ChannelRoleType getRoleTypeByRoleId(String roleId) {
        for (ChannelRoleType roleType : ChannelRoleType.values()) {
            if (roleType.ROLE_ID.equals(roleId)) {
                return roleType;
            }
        }
        return null;
    }

}
