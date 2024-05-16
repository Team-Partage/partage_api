package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelRoleEntity;
import com.egatrap.partage.constants.ChannelRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRoleRepository extends JpaRepository<ChannelRoleEntity, ChannelRoleType> {
}
