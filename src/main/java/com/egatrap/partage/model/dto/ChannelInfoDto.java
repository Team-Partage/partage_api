package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfoDto {

    private Long channelNo;
    private String name;
    private ChannelType type;
    private String hashtag;
    private String channelUrl;
    private String channelColor;
    private LocalDateTime createAt;
}
