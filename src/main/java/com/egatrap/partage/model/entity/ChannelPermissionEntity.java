package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelPermissionType;
import com.egatrap.partage.constants.ChannelRoleType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_permission")
public class ChannelPermissionEntity {
    @Id
    private String permissionId;

    @Column(nullable = false, length = 50)
    private String permission_name;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelPermissionMappingEntity> channelPermissionMappings;

    public ChannelPermissionEntity(ChannelPermissionType roleType) {
        this.permissionId = roleType.getROLE_ID();
        this.permission_name = roleType.name();
    }

    public List<ChannelPermissionEntity> defaultChannelPermissionList() {

        ChannelPermissionType[] channelPermissionTypes = {
                ChannelPermissionType.VIEWER_ADD,
                ChannelPermissionType.VIEWER_CHATSEND,
                ChannelPermissionType.MODERATOR_ADD,
                ChannelPermissionType.MODERATOR_REMOVE,
                ChannelPermissionType.MODERATOR_MOVE,
                ChannelPermissionType.MODERATOR_PLAYANDPAUSE,
                ChannelPermissionType.MODERATOR_SEEK,
                ChannelPermissionType.MODERATOR_SKIP,
                ChannelPermissionType.MODERATOR_CHATSEND,
                ChannelPermissionType.OWNER_ADD,
                ChannelPermissionType.OWNER_REMOVE,
                ChannelPermissionType.OWNER_MOVE,
                ChannelPermissionType.OWNER_PLAYANDPAUSE,
                ChannelPermissionType.OWNER_SEEK,
                ChannelPermissionType.OWNER_SKIP,
                ChannelPermissionType.OWNER_CHATSEND,
                ChannelPermissionType.OWNER_CHATDELETE,
                ChannelPermissionType.OWNER_BAN
        };

        List<ChannelPermissionEntity> defaultChannelPermissionList = new ArrayList<>();
        for (ChannelPermissionType channelPermissionType : channelPermissionTypes) {

            ChannelPermissionEntity channelPermission = new ChannelPermissionEntity(channelPermissionType);
            defaultChannelPermissionList.add(channelPermission);
        }

        return defaultChannelPermissionList;
    }
}
