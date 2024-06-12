package com.egatrap.partage.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {

    private String userId;
    private String email;
    private String username;
    private String nickname;
    private String profileColor;
    private String profileImage;
}
