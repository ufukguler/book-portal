package com.bookportal.api.service;

import com.bookportal.api.entity.EmailConfirm;
import com.bookportal.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailConfirmService emailConfirmService;
    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    String mailAccountName;
    @Value("${server.port}")
    String port;


    public void sendPasswordResetLink(String name, String to, String text) {
        MimeMessagePreparator mailMessage = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSentDate(new Date());
            message.setFrom(mailAccountName, "AppMedia");
            message.setTo(to);
            message.setSubject("AppMedia Password Reset");
            message.setText(generateMailBody(to, text));
        };
        emailSender.send(mailMessage);
    }

    public void sendEmailConfirmationLink(User user) {
        EmailConfirm emailConfirm = emailConfirmService.generateEmailConfirmationKey(user);
        String key = emailConfirm.getSecretKey();

        MimeMessagePreparator mailMessage = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSentDate(new Date());
            message.setFrom(mailAccountName, "AppMedia");
            message.setTo(emailConfirm.getUser().getMail());
            message.setSubject("AppMedia Account Confirmation");
            message.setText(generateMailBody(emailConfirm.getUser().getMail(), key));
        };
        emailSender.send(mailMessage);
    }

    private String generateMailBody(String to, String key) {
        String url = "http://localhost:" + port;
        return url + "/sendMail/confirmEmail?key=" + key + "&email=" + to;
    }
}