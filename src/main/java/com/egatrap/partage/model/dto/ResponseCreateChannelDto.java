package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.model.entity.ChannelPermissionEntity;
import com.egatrap.partage.model.entity.ChannelPermissionMappingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCreateChannelDto {

    private UserInfo userInfo;
    private ChannelInfo channelInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userNo;
        private String email;
        private String nickname;
        private String profileColor;
        private String profileImage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelInfo {
        private Long channelNo;
        private String name;
        private ChannelType type;
        private String hashtag;
        private String channelUrl;
        private String channelColor;
        private LocalDateTime createAt;
        private List<ChannelPermissionEntity> channelPermissionMappingList;
    }
}
