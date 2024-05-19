package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 인증 메일 발송
     */
    @PostMapping("/auth-email")
    public ResponseEntity<?> sendAuthEmail(@Valid @RequestBody RequestSendAuthEmailDto params) {

        // 인증 메일 발송 - Async 메서드
        userService.sendAuthEmail(params);

        return new ResponseEntity<>(
                new ResponseSendAuthEmailDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody RequestJoinDto params) {

        // 회원가입
        boolean isJoin = userService.join(params);

        if (!isJoin) {
            return new ResponseEntity<>(
                    new ResponseJoinDto(ResponseType.FAIL),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ResponseJoinDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }
}