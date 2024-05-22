package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelPermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPermissionInfoDto {

    private ChannelPermissionType channelPermissionType;
}
