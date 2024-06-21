package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountViewerDto {

    private long loginUsers;
    private long anonymousUsers;
    private long totalUsers;


    public CountViewerDto(long loginUsers, long anonymousUsers) {
        this.loginUsers = loginUsers;
        this.anonymousUsers = anonymousUsers;
        this.totalUsers = loginUsers + anonymousUsers;
    }

}
