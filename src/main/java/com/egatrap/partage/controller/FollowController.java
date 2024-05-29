package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 팔로우
     */
    @PostMapping
    public ResponseEntity<?> follow(@Valid @RequestBody RequestFollowDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long fromUserNo = Long.parseLong(authentication.getName());
        Long toUserNo = params.getUserNo();

        // 자기 자신 팔로우인지 체크
        if (fromUserNo.equals(toUserNo))
            throw new BadRequestException("Cannot follow/unfollow yourself.");

        // 팔로우
        followService.follow(fromUserNo, toUserNo);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 언팔로우
     */
    @DeleteMapping
    public ResponseEntity<?> unfollow(@Valid @RequestBody RequestUnfollowDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long fromUserNo = Long.parseLong(authentication.getName());
        Long toUserNo = params.getUserNo();

        // 자기 자신 언팔로우인지 체크
        if (fromUserNo.equals(toUserNo))
            throw new BadRequestException("Cannot follow/unfollow yourself.");

        // 언팔로우
        followService.unfollow(fromUserNo, toUserNo);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 팔로잉 목록 조회
     * - 팔로잉: 내가 팔로우 한 사람
     */
    @GetMapping("/followings")
    public ResponseEntity<?> getFollowingList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        ResponseGetFollowingListDto response = followService.getFollowingList(userNo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 팔로워 목록 조회
     * - 팔로워: 나를 팔로우 한 사람
     */
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowerList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        ResponseGetFollowerListDto response = followService.getFollowerList(userNo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
