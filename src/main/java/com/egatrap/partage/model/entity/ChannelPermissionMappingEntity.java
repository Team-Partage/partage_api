package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_permission_mapping")
public class ChannelPermissionMappingEntity {
    @EmbeddedId
    private ChannelPermissionMappingId id;

    @MapsId("permissionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    @ToString.Exclude
    private ChannelPermissionEntity permission;

    @MapsId("channelNo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_no")
    @ToString.Exclude
    private ChannelEntity channel;
}
