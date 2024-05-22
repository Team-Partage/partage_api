package com.egatrap.partage.model.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class RequestCheckDuplicateNicknameDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
}
