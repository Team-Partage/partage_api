package com.egatrap.partage.repository;

import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.model.entity.ChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, String> {
    Optional<ChannelEntity> findByChannelIdAndIsActive(String channelId, boolean isActive);

    Page<ChannelEntity> findByTypeAndIsActive(ChannelType channelType, boolean isActive, PageRequest pageRequest);

    @Query("SELECT c from ChannelEntity c where c.hashtag like %:keyword% or c.name like %:keyword% and c.isActive = :isActive")
    Page<ChannelEntity> findBySearchKeywordAndIsActive(String keyword, boolean isActive, PageRequest pageRequest);

    boolean existsByChannelIdAndIsActive(String channelId, boolean isActive);
}
