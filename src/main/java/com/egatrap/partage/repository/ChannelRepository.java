package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Optional<ChannelEntity> findByChannelNoAndIsActive(Long channelNo, boolean b);
}
