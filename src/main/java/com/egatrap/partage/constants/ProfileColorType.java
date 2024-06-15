package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileColorType {

    SKYBLUE("#00FFFF"),
    LIGHTGREEAN("#57F3A8"),
    GREEAN("#32BF99"),
    BLUE("#43B0FF"),
    VIOLET("#9C78FF"),
    PINK("#FD68B3"),
    YELLOW("#FFE100"),
    ORANGE("#FFA030"),
    PEACH("#FF7D84");

    private final String PROFILE_COLOR;

    public static ProfileColorType get(String profileColor) {
        for (ProfileColorType roleType : ProfileColorType.values()) {
            if (roleType.PROFILE_COLOR.equals(profileColor)) {
                return roleType;
            }
        }
        return null;
    }

    public static String getProfileColor(String profileColor) {
        for (ProfileColorType profileColorType : ProfileColorType.values()) {
            if (profileColorType.PROFILE_COLOR.equals(profileColor)) {
                return profileColorType.getPROFILE_COLOR();
            }
        }
        // 일치하는 색상이 아니라면 null로 설정
        return null;
    }
}
