package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSessionDto {

    private String id;
    private int currentPlayTime;
    private boolean isPlaying;
    private String lastActiveTime;

}
