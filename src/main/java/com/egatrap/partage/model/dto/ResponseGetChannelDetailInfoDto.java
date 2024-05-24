package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelPermissionType;
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

    private ChannelInfoDto channel;
    private ChannelUserInfoDto user;
    private List<ChannelUserInfoDto> channelUsers;
    private List<PlaylistDto> playlists;
    private List<ChannelPermissionType> channelPermissionTypes;
}
