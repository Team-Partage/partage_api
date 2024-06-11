package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelUserDto {

    private String roleId;
    private String userId;
    private String email;
    private String nickname;
    private String profileColor;
    private String profileImage;
}
