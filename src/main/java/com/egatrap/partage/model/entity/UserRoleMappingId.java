package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.UserRoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class UserRoleMappingId implements Serializable {

    private String roleId;
    private Long userNo;
}