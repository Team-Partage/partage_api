package com.egatrap.partage.service;

import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.FollowDto;
import com.egatrap.partage.model.dto.ResponseGetFollowerListDto;
import com.egatrap.partage.model.dto.ResponseGetFollowingListDto;
import com.egatrap.partage.model.entity.FollowEntity;
import com.egatrap.partage.model.entity.UserEntity;
import com.egatrap.partage.repository.FollowRepository;
import com.egatrap.partage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("followService")
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ModelMapper modelMapper;

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

    @Transactional
    public ResponseGetFollowingListDto getFollowingList(Long userNo) {

        UserEntity requestUser = userRepository.findById(userNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 팔로잉 목록 조회
        List<FollowDto> followings = followRepository.findByFromUser_UserNo(userNo)
                .stream()
                .filter(followEntity -> followEntity.getToUser().getIsActive())
                .map(followEntity -> {
                    FollowDto followDto = modelMapper.map(followEntity.getToUser(), FollowDto.class);
                    followDto.setFollowStatus(followRepository.existsByFromUserAndToUser(requestUser, followEntity.getToUser()));
                    return followDto;
                })
                .toList();

        ResponseGetFollowingListDto responseGetFollowingListDto = new ResponseGetFollowingListDto();
        responseGetFollowingListDto.setFollowings(followings);

        return responseGetFollowingListDto;
    }

    @Transactional
    public ResponseGetFollowerListDto getFollowerList(Long userNo) {

        UserEntity requestUser = userRepository.findById(userNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 팔로워 목록 조회
        List<FollowDto> followers = followRepository.findByToUser_UserNo(userNo)
                .stream()
                .filter(followEntity -> followEntity.getFromUser().getIsActive())
                .map(followEntity -> {
                    FollowDto followDto = modelMapper.map(followEntity.getFromUser(), FollowDto.class);
                    followDto.setFollowStatus(followRepository.existsByFromUserAndToUser(requestUser, followEntity.getFromUser()));
                    return followDto;
                })
                .toList();

        ResponseGetFollowerListDto responseGetFollowerListDto = new ResponseGetFollowerListDto();
        responseGetFollowerListDto.setFollowers(followers);

        return responseGetFollowerListDto;
    }
}
