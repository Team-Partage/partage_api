package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileColorType {

    SKYBLUE("#75FBFD"),
    LIGHTGREEAN("#86F0AE"),
    GREEAN("#61BC9B"),
    BLUE("#62AEF9"),
    VIOLET("#9679F7"),
    PINK("#EB71B1"),
    YELLOW("#FAE24C"),
    ORANGE("#F2A44B"),
    PEACH("#EE8487");

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
