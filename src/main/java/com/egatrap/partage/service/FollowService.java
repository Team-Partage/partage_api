package com.egatrap.partage.service;

import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.entity.FollowEntity;
import com.egatrap.partage.model.entity.UserEntity;
import com.egatrap.partage.repository.FollowRepository;
import com.egatrap.partage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service("followService")
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(Long fromUserNo, Long toUserNo) {

        UserEntity fromUser = userRepository.findById(fromUserNo)
                .orElseThrow(() -> new BadRequestException("User not found."));
        UserEntity toUser = userRepository.findById(toUserNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 중복 팔로우 체크
        if (followRepository.existsByFromUserAndToUser(fromUser, toUser))
            throw new ConflictException("Already follow.");

        FollowEntity follow = FollowEntity.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long fromUserNo, Long toUserNo) {

        UserEntity fromUser = userRepository.findById(fromUserNo)
                .orElseThrow(() -> new BadRequestException("User not found."));
        UserEntity toUser = userRepository.findById(toUserNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        followRepository.deleteFollowByFromUserAndToUser(fromUser, toUser);
    }
}
