package com.egatrap.partage.model.dto;

import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.model.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class RequestJoinDto {

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String username;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$", message = "비밀번호는 8-16 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(email)
                .username(username)
                .nickname(nickname)
                .password(password)
                .isActive(true)
                .build();
    }
}
