package com.egatrap.partage.model.dto.userdto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class SendAuthEmailRequestDto {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
}
