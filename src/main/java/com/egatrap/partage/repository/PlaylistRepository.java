package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.PlaylistEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {
    int countByChannel_ChannelIdAndIsActive(String channelId, boolean isActive);

    List<PlaylistEntity> findByChannel_ChannelIdAndIsActiveOrderBySequence(String channelId, boolean isActive, Pageable pageable);

    List<PlaylistEntity> findAllByChannel_ChannelIdAndIsActiveOrderBySequence(String channelId, boolean isActive);

    Optional<PlaylistEntity> findFirstByChannel_ChannelIdAndIsActiveAndSequence(String channelId, boolean isActive, int sequence);

    boolean existsByChannel_ChannelIdAndPlaylistNoAndIsActive(String channelId, Long playlistNo, boolean b);
}
