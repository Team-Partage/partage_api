package com.egatrap.partage.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@Embeddable
public class ChannelRoleMappingId implements Serializable {
    private String roleId;
    private Long channelNo;
    private Long userNo;
}
