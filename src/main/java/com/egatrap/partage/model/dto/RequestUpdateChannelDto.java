package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.model.entity.ChannelEntity;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class RequestUpdateChannelDto {

    @NotBlank(message = "채널명을 입력해주세요.")
    private String name;

    @NotNull(message = "채널 타입을 선택해주세요.")
    private ChannelType type;

    private String hashtag;

    private String channelColor;

    public ChannelEntity toEntity(String url) {
        return ChannelEntity.builder()
                .name(name)
                .type(type)
                .hashtag(hashtag)
                .channelColor(channelColor)
                .channelUrl(url)
                .isActive(true)
                .build();
    }
}
