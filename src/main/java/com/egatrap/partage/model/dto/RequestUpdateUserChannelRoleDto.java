package com.egatrap.partage.model.dto;

import com.egatrap.partage.constants.ChannelRoleType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class RequestUpdateUserChannelRoleDto {

    @NotNull
    private Long userNo;

    @NotBlank
    private String roleId;
}
