package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelPermissionType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_permission")
public class ChannelPermissionEntity {
    @Id
    @Enumerated(EnumType.STRING)
    private ChannelPermissionType permissionId;

    @Column(nullable = false, length = 50)
    private String permission_name;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelPermissionMappingEntity> channelPermissionMappings;
}
