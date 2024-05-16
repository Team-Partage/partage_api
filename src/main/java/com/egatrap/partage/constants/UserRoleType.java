package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRoleType {

    ROLE_SUPER_ADMIN("R0000"),
    ROLE_ADMIN("R0100"),
    ROLE_USER("R0200");

    private final String ROLE_ID;

    public static UserRoleType of(String id) {
        for (UserRoleType userRoleType : values()) {
            if (userRoleType.ROLE_ID.equals(id)) {
                return userRoleType;
            }
        }
        throw new IllegalArgumentException("Unknown user role id: " + id);
    }

}
