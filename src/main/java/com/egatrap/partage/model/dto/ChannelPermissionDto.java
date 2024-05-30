package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelPermissionDto {
    private String channelId;
    private ChannelRoleType playlistAdd;
    private ChannelRoleType playlistRemove;
    private ChannelRoleType playlistMove;
    private ChannelRoleType videoPlay;
    private ChannelRoleType videoSeek;
    private ChannelRoleType videoSkip;
    private ChannelRoleType chatSend;
    private ChannelRoleType chatDelete;
    private ChannelRoleType ban;
}
