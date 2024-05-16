package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelRoleType;
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
    @Enumerated(EnumType.STRING)
    private ChannelRoleType roleId;

    @Column(nullable = false, length = 20)
    private String roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChannelRoleMappingEntity> channelRoleMappings;
}