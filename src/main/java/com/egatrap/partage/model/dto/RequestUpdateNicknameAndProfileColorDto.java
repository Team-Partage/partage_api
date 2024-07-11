package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestUpdateNicknameAndProfileColorDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Column(length = 20)
    @NotBlank(message = "프로필 색상을 골라주세요.")
    private String profileColor;
}
