package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.FollowService;
import com.egatrap.partage.service.UserService;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입 - 인증 메일 전송")
    @PostMapping("/auth-email")
    public ResponseEntity<?> sendAuthEmail(@Valid @RequestBody RequestSendAuthEmailDto params) {

        // 인증 메일 발송
        userService.sendAuthEmail(params);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "회원가입 - 인증 번호 체크")
    @PostMapping("/auth-number")
    public ResponseEntity<?> checkAuthNumber(@Valid @RequestBody RequestCheckAuthNumberDto params) {

        // 인증 번호 체크
        if (!userService.checkAuthNumber(params.getEmail(), params.getAuthNumber()))
            throw new BadRequestException("Invalid auth number.");

        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 중복 확인")
    @GetMapping("/check-email")
    public ResponseEntity<?> checkDuplicateEmail(@Valid @RequestBody RequestCheckDuplicateEmailDto params) {

        // 이메일 중복 확인
        boolean isExisted = userService.isExistEmail(params.getEmail());
        // 이메일 중복
        if (isExisted)
            throw new ConflictException("The resource already exists. email=" + params.getEmail());

        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "닉네임 중복 확인")
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkDuplicateNickname(@Valid @RequestBody RequestCheckDuplicateNicknameDto params) {

        // 닉네임 중복 확인
        boolean isExisted = userService.isExistNickname(params.getNickname());
        // 닉네임 중복
        if (isExisted)
            throw new ConflictException("The resource already exists. nickname=" + params.getNickname());

        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody RequestJoinDto params) {

        if (userService.isExistEmail(params.getEmail()) || userService.isExistNickname(params.getNickname()))
            throw new ConflictException("The user already exists. Please check the email and nickname again.");

        // 회원가입
        userService.join(params);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ResponseGetUserInfoDto response = userService.findUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<?> deactiveUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        userService.deactiveUser(userId);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "닉네임 수정")
    @PatchMapping("/me/nickname")
    public ResponseEntity<?> updateNickname(@Valid @RequestBody RequestUpdateNicknameDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        userService.updateNickname(userId, params.getNickname());
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }
}