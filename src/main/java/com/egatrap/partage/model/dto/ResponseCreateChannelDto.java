package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCreateChannelDto {

    private ChannelDto channel;
    private List<ChannelUserDto> channelUsers;
    private ChannelPermissionInfoDto channelPermissions;
}
