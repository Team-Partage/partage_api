package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.UserSessionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserSessionRepository extends CrudRepository<UserSessionEntity, String> {
//    long countByChannelId(String channelId);

    Iterable<UserSessionEntity> findAllByChannelId(String channelId);

}
