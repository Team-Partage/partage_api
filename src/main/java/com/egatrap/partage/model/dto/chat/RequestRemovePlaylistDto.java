package com.egatrap.partage.model.dto.chat;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRemovePlaylistDto {

    @NotBlank
    private Long playlistNo;

}
