package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
     * 이메일 중복 확인
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkDuplicateEmail(@PathVariable("email") String email) {

        // 이메일 중복 확인
        boolean isExisted = userService.isExistEmail(email);

        // 이메일 중복
        if (isExisted) {
            return new ResponseEntity<>(ErrorMessageDto.builder()
                    .code(409)
                    .status("CONFLICT")
                    .message("The resource already exists")
                    .build(),
                    HttpStatus.CONFLICT);
        }

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
        if (isExisted) {
            return new ResponseEntity<>(ErrorMessageDto.builder()
                    .code(409)
                    .status("CONFLICT")
                    .message("The resource already exists")
                    .build(),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody RequestJoinDto params) {

        if (isInvalidJoinRequest(params)) {
            return new ResponseEntity<>(
                    ErrorMessageDto.builder()
                    .code(400)
                    .status("BAD REQUEST")
                    .message("Invalid join request")
                    .build(),
                    HttpStatus.BAD_REQUEST);
        }

        // 회원가입
        userService.join(params);
        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }

    private boolean isInvalidJoinRequest(RequestJoinDto params) {

        return userService.isExistEmail(params.getEmail()) ||
                userService.isExistNickname(params.getNickname()) ||
                !params.getPassword().equals(params.getPasswordConfirm()) ||
                !userService.checkAuthNumber(params.getEmail(), params.getAuthNumber());
    }

    /**
     * 회원가입 백도어 - 이메일인증 X
     */
    @PostMapping("/join-backdoor")
    public ResponseEntity<?> joinBackdoor(@Valid @RequestBody RequestJoinDto params) {

        // 회원가입
        userService.join(params);
        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }
}