package com.egatrap.partage.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_chatting")
public class ChattingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chattingNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_no", nullable = false)
    @ToString.Exclude
    private ChannelEntity channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    @ToString.Exclude
    private UserEntity user;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
}
