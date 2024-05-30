package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelPermissionRepository extends JpaRepository<ChannelPermissionEntity, String> {
    Optional<ChannelPermissionEntity> findByChannelId(String channelId);
}
