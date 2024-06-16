package com.egatrap.partage.service;

import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.constants.ProfileColorType;
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
import java.security.SecureRandom;
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

        UserEntity user = UserEntity.builder()
                .userId(CodeGenerator.generateID("U"))
                .email(params.getEmail())
                .username(params.getUsername())
                .nickname(params.getNickname())
                .password(passwordEncoder.encode(params.getPassword()))
                .isActive(true)
                .build();
        userRepository.save(user);

        UserRoleEntity userRole = new UserRoleEntity(UserRoleType.ROLE_USER);

        UserRoleMappingId userRoleMappingId = new UserRoleMappingId();
        userRoleMappingId.setUserId(user.getUserId());
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
    public ResponseGetUserInfoDto findUser(String userId) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        return new ResponseGetUserInfoDto(userDto);
    }

    @Transactional
    public void deactiveUser(String userId) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        userEntity.deactive();
        userRepository.save(userEntity);

        // ToDo. 팔로잉/팔로워 목록 전체 제거 필요
    }

    @Transactional
    public void updateNickname(String userId, String nickname) {

        if (userRepository.findByNicknameAndIsActive(nickname, true).isPresent())
            throw new ConflictException("Nickname is already in exists or is currently in use.");

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        userEntity.updateNickname(nickname);
        userRepository.save(userEntity);
    }

    @Transactional
    public void updatePassword(String userId, String currentPassword, String newPassword) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 현재 비밀번호가 일치하지 않을 때
        if (!passwordEncoder.matches(currentPassword, userEntity.getPassword()))
            throw new BadRequestException("The current password you entered is incorrect.");

        // 비밀번호 수정
        userEntity.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    @Transactional
    public void updateProfileColor(String userId, String profileColor) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 프로필 색상 수정
        userEntity.updateProfileColor(ProfileColorType.getProfileColor(profileColor));
        userRepository.save(userEntity);
    }

    @Transactional
    public void findPassword(String email) {

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 임시 비밀번호 생성
        String tempPassword = getTempPassword();

        // 임시 비밀번호 메일 발송
        sendTempPasswordEmail(email, tempPassword);

        // 비밀번호 수정
        userEntity.updatePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(userEntity);
    }

    @Async
    private void sendTempPasswordEmail(String email, String tempPassword) {

        String toEmail = email;
        String setFrom = "feat240513@gmail.com";
        String title = "[Partage] 임시 비밀번호 발급 이메일";
        String content =
                "Partage를 방문해주셔서 감사합니다." +
                        "<br><br>" +
                        "임시 비밀번호는 " + tempPassword + "입니다." +
                        "<br><br>" +
                        "로그인 후 비밀번호를 변경해주세요." +
                        "<br>";

        MimeMessage message = mailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("인증 이메일 발송 실패" + e.getMessage(), e);
        }
    }

    private String getTempPassword() {

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String special_chars = "@$!%*#?&";
        String all_chars = upper + lower + digits + special_chars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 최소 8자의 임시 비밀번호 생성
        int length = 8;

        // 적어도 하나의 영문자, 숫자, 특수문자 를 추가
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special_chars.charAt(random.nextInt(special_chars.length())));
        // 나머지 문자 추가
        for (int i = 0; i < length - 3; i++) {
            password.append(all_chars.charAt(random.nextInt(all_chars.length())));
        }

        // 임시 비밀번호를 무작위로 섞음
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(length);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }

        return password.toString();
    }

    @Transactional
    public void updateProfileImage(String userId, String url) {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found."));

        // 프로필 색상 수정
        userEntity.updateProfileImage(url);
        userRepository.save(userEntity);
    }
}
