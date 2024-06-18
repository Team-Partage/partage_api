package com.egatrap.partage.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPlayVideoDto {

    private String palylistId;

    @Min(0)
    private int playTime;

    private boolean playing;
}
