package com.egatrap.partage.repository;

import com.egatrap.partage.model.entity.FollowEntity;
import com.egatrap.partage.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    List<FollowEntity> findByFromUser_UserNo(Long userNo);
    List<FollowEntity> findByToUser_UserNo(Long userNo);

    //Optional<FollowEntity> findByFromUserAndToUser(UserEntity fromUser, UserEntity toUser);
    boolean existsByFromUserAndToUser(UserEntity fromUser, UserEntity toUser);
    void deleteFollowByFromUserAndToUser(UserEntity fromUser, UserEntity toUser);
}
