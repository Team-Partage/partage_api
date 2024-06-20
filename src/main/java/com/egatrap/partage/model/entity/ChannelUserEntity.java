package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "tb_channel_user")
public class ChannelUserEntity {

    @EmbeddedId
    private ChannelUserId id;

    @MapsId("channelId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    @ToString.Exclude
    private ChannelEntity channel;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private ChannelRoleEntity role;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private Boolean isOnline;

    public void updateChannelRoleEntity(ChannelRoleEntity channelRoleEntity) {
        this.role = channelRoleEntity;
    }
}
