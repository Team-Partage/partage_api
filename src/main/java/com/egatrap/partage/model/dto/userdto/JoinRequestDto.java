package com.egatrap.partage.model.dto.userdto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class JoinRequestDto {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^\\d{6}$", message = "인증번호는 숫자 6자리 형식입니다.")
    @NotBlank(message = "인증번호를 입력해주세요.")
    private String authNumber;

    @NotBlank(message = "이름을 입력해주세요.")
    private String username;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$", message = "비밀번호는 8-16 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$", message = "비밀번호는 8-16 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;
}
