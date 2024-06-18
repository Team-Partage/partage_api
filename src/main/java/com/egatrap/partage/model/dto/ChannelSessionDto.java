package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSessionDto {

    private String id;
    private int playTime;
    private LocalDateTime updateTime;
    private boolean isPlaying;
    private String lastActiveTime;

}
