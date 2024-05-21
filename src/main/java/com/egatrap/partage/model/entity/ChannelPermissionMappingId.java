package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelPermissionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Embeddable
public class ChannelPermissionMappingId implements Serializable {
    private String permissionId;
    private Long channelNo;
}
