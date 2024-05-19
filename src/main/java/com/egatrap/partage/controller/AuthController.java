package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.model.dto.RequestLoginDto;
import com.egatrap.partage.model.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Validated @RequestBody RequestLoginDto params) {


        ResponseDto response = new ResponseDto(ResponseType.SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
