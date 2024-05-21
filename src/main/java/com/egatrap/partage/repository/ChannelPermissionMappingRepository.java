package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelPermissionMappingEntity;
import com.egatrap.partage.model.entity.ChannelPermissionMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelPermissionMappingRepository extends JpaRepository<ChannelPermissionMappingEntity, ChannelPermissionMappingId> {
}
