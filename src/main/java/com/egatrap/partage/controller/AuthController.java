package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.JwtTokenService;
import com.egatrap.partage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {


    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@Validated @RequestBody RequestLoginDto params) {

        // Get authentication
        Authentication authentication = userService.login(params);

        // Check authentication result and return error message if failed
        if (authentication == null) {
            return new ResponseEntity<>(ErrorMessageDto.builder()
                    .code(401)
                    .status("UNAUTHORIZED")
                    .message("Invalid email or password")
                    .build(),
                    HttpStatus.UNAUTHORIZED);
        }

        // Generate token and Save token to Redis
        JwtTokenMappingDto jwtTokenMappingDto = jwtTokenService.generateToken(authentication);

        // Return token to client
        return new ResponseEntity<>(ResponseLoginDto.builder()
                .accessToken(jwtTokenMappingDto.getAccessToken())
                .refreshToken(jwtTokenMappingDto.getRefreshToken())
                .build(), HttpStatus.OK);
    }

}
