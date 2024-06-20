package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelColorType;
import com.egatrap.partage.constants.ChannelType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "tb_channel")
public class ChannelEntity {
    @Id
    @Column(columnDefinition = "CHAR(32)", nullable = false)
    private String channelId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(length = 255)
    private String hashtag;

    @Column(nullable = false, length = 255)
    private String channelUrl;

    @Column(length = 20)
    private String channelColor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    @Column(nullable = false)
    private Boolean isActive;

    @Setter
    @Column(nullable = false)
    private Integer viewerCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_no")
    private PlaylistEntity currentPlaylist;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelRoleMappingEntity> channelRoleMappings;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelUserEntity> channelUsers;

    @OneToOne(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    @ToString.Exclude
    private ChannelPermissionEntity channelPermission;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChattingEntity> chattings;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelBanListEntity> banList;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<PlaylistEntity> playlists;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    public void updateChannelInfo(String name, ChannelType type, String hashtag, String channelColor) {
        this.name = name;
        this.type = type;
        this.hashtag = hashtag;
        this.channelColor = channelColor;
        this.updateAt = LocalDateTime.now();
    }

    public void deleteChannel() {
        this.isActive = false;
    }
}