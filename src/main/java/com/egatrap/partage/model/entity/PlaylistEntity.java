package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_playlist")
public class PlaylistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @ToString.Exclude
    private ChannelEntity channel;

    @Setter
    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false, length = 1000)
    private String thumbnail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Setter
    @Column(nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
}
