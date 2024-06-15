package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelSearchDto {

    private ChannelDto channel;
    private PlaylistDto playlist;
    private UserDto owner;
    private int user_count;
}
