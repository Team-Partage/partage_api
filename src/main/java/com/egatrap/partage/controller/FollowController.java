package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.FollowService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "팔로우")
    @PostMapping
    public ResponseEntity<?> follow(@Valid @RequestBody RequestFollowDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String fromUserId = authentication.getName();
        String toUserId = params.getUserId();

        // 자기 자신 팔로우인지 체크
        if (fromUserId.equals(toUserId))
            throw new BadRequestException("Cannot follow/unfollow yourself.");

        // 팔로우
        followService.follow(fromUserId, toUserId);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "언팔로우")
    @DeleteMapping
    public ResponseEntity<?> unfollow(@Valid @RequestBody RequestUnfollowDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String fromUserId = authentication.getName();
        String toUserId = params.getUserId();

        // 자기 자신 언팔로우인지 체크
        if (fromUserId.equals(toUserId))
            throw new BadRequestException("Cannot follow/unfollow yourself.");

        // 언팔로우
        followService.unfollow(fromUserId, toUserId);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "팔로잉 목록 조회")
    @GetMapping("/followings")
    public ResponseEntity<?> getFollowingList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ResponseGetFollowingListDto response = followService.getFollowingList(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "팔로워 목록 조회")
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowerList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ResponseGetFollowerListDto response = followService.getFollowerList(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
