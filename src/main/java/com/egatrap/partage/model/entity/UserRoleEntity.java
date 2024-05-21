package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.UserRoleType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_user_role")
public class UserRoleEntity {
    @Id
    private String roleId;

    @Column(nullable = false, length = 20)
    private String roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<UserRoleMappingEntity> userRoleMappings;

    public UserRoleEntity(UserRoleType roleType) {
        this.roleId = roleType.getROLE_ID();
        this.roleName = roleType.name();
    }
}