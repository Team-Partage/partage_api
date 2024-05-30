package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_ban_list")
public class ChannelBanListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long banNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @ToString.Exclude
    private ChannelEntity channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banned_user_id", nullable = false)
    @ToString.Exclude
    private UserEntity bannedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banned_by_user_id", nullable = false)
    @ToString.Exclude
    private UserEntity bannedByUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
}
