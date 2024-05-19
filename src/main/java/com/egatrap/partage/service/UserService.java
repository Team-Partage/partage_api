package com.egatrap.partage.service;

import com.egatrap.partage.model.entity.AuthEmail;
import com.egatrap.partage.repository.AuthEmailRepository;
import com.egatrap.partage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthEmailRepository authEmailRepository;
    private final JavaMailSender mailSender;

    @Async
    public void sendAuthEmail(String toEmail) {

        String authNumber = makeAuthNumber();
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
}
