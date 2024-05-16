package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.ChannelBanListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelBanListRepository extends JpaRepository<ChannelBanListEntity, Long> {
}
