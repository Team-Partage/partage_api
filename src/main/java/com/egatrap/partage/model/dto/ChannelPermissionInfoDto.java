package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.entity.ChannelPermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPermissionInfoDto {

    private String playlistAdd;
    private String playlistRemove;
    private String playlistMove;
    private String videoPlay;
    private String videoSeek;
    private String videoSkip;
    private String chatSend;
    private String chatDelete;
    private String ban;

    public ChannelPermissionInfoDto(ChannelPermissionEntity channelPermission) {
        this.playlistAdd = channelPermission.getPlaylistAdd();
        this.playlistRemove = channelPermission.getPlaylistRemove();
        this.playlistMove = channelPermission.getPlaylistMove();
        this.videoPlay = channelPermission.getVideoPlay();
        this.videoSeek = channelPermission.getVideoSeek();
        this.videoSkip = channelPermission.getVideoSkip();
        this.chatSend = channelPermission.getChatSend();
        this.chatDelete = channelPermission.getChatDelete();
        this.ban = channelPermission.getBan();
    }
}
