package com.egatrap.partage.controller;

import com.egatrap.partage.model.dto.userdto.SendAuthEmailRequestDto;
import com.egatrap.partage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void sendAuthEmail(@Valid @RequestBody SendAuthEmailRequestDto sendAuthEmailRequestDto) {

        // 인증 메일 발송 - Async 메서드
        userService.sendAuthEmail(sendAuthEmailRequestDto.getEmail());
        // ToDo. add response
    }

    /**
     * 회원가입
     */
    @PostMapping("/join")
    public void join()
    {

    }
}