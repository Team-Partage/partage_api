package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelUserEntity;
import com.egatrap.partage.model.entity.ChannelUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUserEntity, ChannelUserId> {

}
