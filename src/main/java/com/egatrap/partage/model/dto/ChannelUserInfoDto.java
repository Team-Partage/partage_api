package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelUserInfoDto {

    private String roleId;
    private Long userNo;
    private String email;
    private String nickname;
    private String profileColor;
    private String profileImage;

}
