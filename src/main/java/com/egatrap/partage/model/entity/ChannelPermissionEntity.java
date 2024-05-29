package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelRoleType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_permission")
@ToString
public class ChannelPermissionEntity {

    @Id
    @Column(columnDefinition = "CHAR(32)", nullable = false)
    private String channelId;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String playlistAdd;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String playlistRemove;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String playlistMove;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String videoPlay;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String videoSeek;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String videoSkip;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String chatSend;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String chatDelete;

    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String ban;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "channelId")
    private ChannelEntity channel;

    @PrePersist
    protected void onCreate() {
        this.playlistAdd = ChannelRoleType.ROLE_OWNER.getROLE_ID();
        this.playlistRemove = ChannelRoleType.ROLE_OWNER.getROLE_ID();
        this.playlistMove = ChannelRoleType.ROLE_OWNER.getROLE_ID();
        this.videoPlay = ChannelRoleType.ROLE_MODERATOR.getROLE_ID();
        this.videoSeek = ChannelRoleType.ROLE_MODERATOR.getROLE_ID();
        this.videoSkip = ChannelRoleType.ROLE_MODERATOR.getROLE_ID();
        this.chatSend = ChannelRoleType.ROLE_VIEWER.getROLE_ID();
        this.chatDelete = ChannelRoleType.ROLE_MODERATOR.getROLE_ID();
        this.ban = ChannelRoleType.ROLE_OWNER.getROLE_ID();
    }
}
