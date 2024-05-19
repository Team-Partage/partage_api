package com.egatrap.partage.service;

import com.egatrap.partage.model.dto.RequestLoginDto;
import com.egatrap.partage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Authentication login(RequestLoginDto params) {
        try {
            return authenticationManagerBuilder.getObject()
                    .authenticate(new UsernamePasswordAuthenticationToken(params.getEmail(), params.getPassword()));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

}
