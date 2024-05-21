package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelPermissionType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@Embeddable
public class ChannelPermissionMappingId implements Serializable {
    private String permissionId;
    private Long channelNo;
}
