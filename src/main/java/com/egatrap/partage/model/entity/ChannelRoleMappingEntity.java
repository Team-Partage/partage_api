package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_role_mapping")
public class ChannelRoleMappingEntity {
    @EmbeddedId
    private ChannelRoleMappingId id;

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

    @Setter
    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private ChannelRoleEntity role;

    @Column(nullable = false)
    private Boolean isActive;

    public void updateChannelRoleEntity(ChannelRoleEntity channelRoleEntity) {
        this.role = channelRoleEntity;
    }
}
