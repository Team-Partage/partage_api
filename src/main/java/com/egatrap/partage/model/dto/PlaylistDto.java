package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistDto {

    private Long playlistNo;
    private Long channelNo;
    private Integer sequence;
    private String title;
    private String url;
    private String thumbnail;
    private String createAt;

}
