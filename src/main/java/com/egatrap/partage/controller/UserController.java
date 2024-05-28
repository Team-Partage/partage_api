package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.FollowService;
import com.egatrap.partage.service.UserService;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    /**
     * 인증 메일 전송
     */
    @PostMapping("/auth-email")
    public ResponseEntity<?> sendAuthEmail(@Valid @RequestBody RequestSendAuthEmailDto params) {

        // 인증 메일 발송
        userService.sendAuthEmail(params);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 인증 번호 체크
     */
    @PostMapping("/auth-number")
    public ResponseEntity<?> checkAuthNumber(@Valid @RequestBody RequestCheckAuthNumberDto params) {

        // 인증 번호 체크
        if (!userService.checkAuthNumber(params.getEmail(), params.getAuthNumber()))
            throw new BadRequestException("Invalid auth number.");

        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkDuplicateEmail(@PathVariable("email") String email) {

        // 이메일 중복 확인
        boolean isExisted = userService.isExistEmail(email);

        // 이메일 중복
        if (isExisted)
            throw new ConflictException("The resource already exists. email=" + email);

        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 닉네임 중복 확인
     */
    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<?> checkDuplicateNickname(@PathVariable("nickname") String nickname) {

        // 닉네임 중복 확인
        boolean isExisted = userService.isExistNickname(nickname);

        // 닉네임 중복
        if (isExisted)
            throw new ConflictException("The resource already exists. nickname=" + nickname);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody RequestJoinDto params) {

        if (userService.isExistEmail(params.getEmail()) || userService.isExistNickname(params.getNickname()))
            throw new ConflictException("The user already exists. Please check the email and nickname again.");

        // 회원가입
        userService.join(params);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    /**
     * 팔로우
     */
    @PostMapping("/follow")
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
    @DeleteMapping("/follow")
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
     *  - 팔로잉: 내가 팔로우 한 사람
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
     *  - 팔로워: 나를 팔로우 한 사람
     */
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowerList() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        ResponseGetFollowerListDto response = followService.getFollowerList(userNo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 회원 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        ResponseGetUserInfoDto response = userService.findUser(userNo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}