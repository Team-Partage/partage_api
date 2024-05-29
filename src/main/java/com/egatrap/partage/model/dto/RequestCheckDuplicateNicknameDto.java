package com.egatrap.partage.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestCheckDuplicateNicknameDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
}
