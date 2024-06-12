package com.egatrap.partage.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelColorType {

    SKYBLUE("#75FBFD"),
    LIGHTGREEAN("#86F0AE"),
    YELLOW("#FAE24C"),
    PEACH("#EE8487"),
    VIOLET("#9679F7");

    private final String CHANNEL_COLOR;

    public static ChannelColorType get(String channelColor) {
        for (ChannelColorType channelColorType : ChannelColorType.values()) {
            if (channelColorType.CHANNEL_COLOR.equals(channelColor)) {
                return channelColorType;
            }
        }
        return null;
    }

    public static String getChannelColor(String channelColor) {
        for (ChannelColorType channelColorType : ChannelColorType.values()) {
            if (channelColorType.CHANNEL_COLOR.equals(channelColor)) {
                return channelColorType.getCHANNEL_COLOR();
            }
        }
        // 일치하는 색상이 아니라면 null로 설정
        return null;
    }
}
