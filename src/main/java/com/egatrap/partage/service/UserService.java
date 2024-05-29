package com.egatrap.partage.service;

import com.egatrap.partage.constants.UserRoleType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.UserRepository;
import com.egatrap.partage.repository.UserRoleMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.egatrap.partage.repository.AuthEmailRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthEmailRepository authEmailRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;

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

    @Async
    public void sendAuthEmail(RequestSendAuthEmailDto params) {

        String authNumber = makeAuthNumber();
        String toEmail = params.getEmail();
        String setFrom = "feat240513@gmail.com";
        String title = "[Partage] 회원가입 인증 이메일";
        String content =
                "Partage를 방문해주셔서 감사합니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>";

        MimeMessage message = mailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);

            AuthEmail authEmail = new AuthEmail(toEmail, authNumber);
            authEmailRepository.save(authEmail);

        } catch (MessagingException e) {
            throw new RuntimeException("인증 이메일 발송 실패" + e.getMessage(), e);
        }
    }

    private String makeAuthNumber() {

        Random random = new Random();
        StringBuilder authNumber = new StringBuilder();
        for (int i = 0; i < 6; i++)
            authNumber.append(Integer.toString(random.nextInt(10)));

        return authNumber.toString();
    }

    @Transactional
    public void join(RequestJoinDto params) {

        params.setPassword(passwordEncoder.encode(params.getPassword()));
        UserEntity user = params.toEntity();
        userRepository.save(user);

        UserRoleEntity userRole = new UserRoleEntity(UserRoleType.ROLE_USER);

        UserRoleMappingId userRoleMappingId = new UserRoleMappingId();
        userRoleMappingId.setUserNo(user.getUserNo());
        userRoleMappingId.setRoleId(userRole.getRoleId());

        UserRoleMappingEntity userRoleMappingEntity = new UserRoleMappingEntity(userRoleMappingId, user, userRole);
        userRoleMappingRepository.save(userRoleMappingEntity);
    }

    public boolean checkAuthNumber(String email, String authNumber) {

        Optional<AuthEmail> optionalAuthEmail = authEmailRepository.findById(email);
        return optionalAuthEmail.isPresent() &&
                email.equals(optionalAuthEmail.get().getEmail()) &&
                authNumber.equals(optionalAuthEmail.get().getAuthNum());
    }

    @Transactional
    public boolean isExistEmail(String email) {

        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public boolean isExistNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public ResponseGetUserInfoDto findUser(Long userNo) {

        UserEntity userEntity = userRepository.findById(userNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        return new ResponseGetUserInfoDto(userDto);
    }

    @Transactional
    public void deactiveUser(Long userNo) {

        UserEntity userEntity = userRepository.findById(userNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        userEntity.deactive();
        userRepository.save(userEntity);
    }

    @Transactional
    public void updateNickname(Long userNo, String nickname) {

        if (userRepository.findByNicknameAndIsActive(nickname, true).isPresent())
            throw new ConflictException("Nickname is already in exists or is currently in use.");

        UserEntity userEntity = userRepository.findById(userNo)
                .orElseThrow(() -> new BadRequestException("User not found."));

        userEntity.updateNickname(nickname);
        userRepository.save(userEntity);
    }
}
