package com.egatrap.partage.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private String nickname;

    private String profileColor;

    private String profileImage;

    @NotBlank
    private String message;

}
