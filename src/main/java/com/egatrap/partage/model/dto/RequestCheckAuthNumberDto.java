package com.egatrap.partage.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class RequestCheckAuthNumberDto {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^\\d{6}$", message = "인증번호는 숫자 6자리 형식입니다.")
    @NotBlank(message = "인증번호를 입력해주세요.")
    private String authNumber;
}
