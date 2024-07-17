package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetChannelDetailInfoDto {

    private ChannelDto channel;
    private ChannelUserDto owner;
    private ChannelUserDto user;
    private ChannelPermissionInfoDto channelPermissions;
}
