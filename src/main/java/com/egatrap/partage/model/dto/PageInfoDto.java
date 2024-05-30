package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageInfoDto {

    private int cursor;
    private int perPage;
    private int totalPage;
    private Long totalCount;
}
