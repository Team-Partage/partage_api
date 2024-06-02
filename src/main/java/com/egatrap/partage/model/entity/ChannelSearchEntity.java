package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelType;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Immutable
@Entity
@Table(name = "vw_channel_search")
@ToString
public class ChannelSearchEntity {

    @Id
    private String channelId;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private String hashtag;

    private String channelUrl;

    private String channelColor;

    private LocalDateTime createAt;

    private String titles;
}
