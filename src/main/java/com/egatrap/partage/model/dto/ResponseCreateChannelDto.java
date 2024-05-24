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
public class ResponseCreateChannelDto {

    private ChannelInfoDto channel;
    private List<ChannelUserInfoDto> channelUsers;
    private List<ChannelPermissionType> channelPermissionTypes;
}
