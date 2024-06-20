package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.UserRoleType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_channel_role")
public class ChannelRoleEntity {
    @Id
    @Column(columnDefinition = "CHAR(5)", nullable = false)
    private String roleId;

    @Column(nullable = false, length = 20)
    private String roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelRoleMappingEntity> channelRoleMappings;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelUserEntity> channelUsers;

    public ChannelRoleEntity(ChannelRoleType roleType) {
        this.roleId = roleType.getROLE_ID();
        this.roleName = roleType.name();
    }
}