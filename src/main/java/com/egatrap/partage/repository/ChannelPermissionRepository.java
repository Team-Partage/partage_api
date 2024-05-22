package com.egatrap.partage.repository;

import com.egatrap.partage.constants.ChannelPermissionType;
import com.egatrap.partage.model.entity.ChannelPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelPermissionRepository extends JpaRepository<ChannelPermissionEntity, ChannelPermissionType> {
}
