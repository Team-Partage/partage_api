package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFromUser_UserNo(Long userNo);
    List<FollowEntity> findByToUser_UserNo(Long userNo);
}
