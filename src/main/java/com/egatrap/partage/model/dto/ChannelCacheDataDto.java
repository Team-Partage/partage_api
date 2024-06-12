package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelCacheDataDto {

    private String channelId;
    private ChannelDto channel;
    private List<UserDto> users;
    private Integer currentPlayTime;
    private Boolean isPlaying;
    private LocalDateTime lastUpdateAt;

}
