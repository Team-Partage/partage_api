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
public class ResponseGetChannelDetailInfoDto {

    private ChannelDto channel;
    private List<ChannelUserDto> channelUsers;
    private List<PlaylistDto> playlists;
    private ChannelPermissionInfoDto channelPermissions;
}
