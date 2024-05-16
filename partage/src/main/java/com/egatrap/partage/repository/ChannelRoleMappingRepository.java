package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelRoleMappingEntity;
import com.egatrap.partage.model.entity.ChannelRoleMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRoleMappingRepository extends JpaRepository<ChannelRoleMappingEntity, ChannelRoleMappingId> {
}
