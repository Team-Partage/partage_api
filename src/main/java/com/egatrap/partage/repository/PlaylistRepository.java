package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.PlaylistEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {
    int countByChannel_ChannelNoAndIsActive(Long channelNo, boolean isActive);

    List<PlaylistEntity> findByChannel_ChannelNoAndIsActiveOrderBySequence(long channelId, boolean isActive, Pageable pageable);
}
