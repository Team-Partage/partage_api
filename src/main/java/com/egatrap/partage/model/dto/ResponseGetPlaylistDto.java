package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseGetPlaylistDto {

    private List<PlaylistDto> playlists;
    private Long totalContents;
    private Integer pageSize;
    private Integer page;

}
