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

    @MapsId("channelNo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_no")
    @ToString.Exclude
    private ChannelEntity channel;

    @MapsId("userNo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    @ToString.Exclude
    private UserEntity user;

    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private ChannelRoleEntity role;
}
