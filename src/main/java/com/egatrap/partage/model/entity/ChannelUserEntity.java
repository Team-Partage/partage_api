package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    @ToString.Exclude
    private ChannelEntity channel;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private ChannelRoleEntity role;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private Long onlineCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime lastAccessAt;

    public void increaseOnlineCount() {
        this.onlineCount++;
    }

    public void decreaseOnlineCount() {
        this.onlineCount = Math.max(0, this.onlineCount - 1);
    }

    public void updateLastAccessAt() {
        this.lastAccessAt = LocalDateTime.now();
    }
}
