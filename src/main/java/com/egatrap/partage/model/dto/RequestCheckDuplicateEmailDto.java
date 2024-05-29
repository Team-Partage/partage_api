package com.egatrap.partage.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestCheckDuplicateEmailDto {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
}
