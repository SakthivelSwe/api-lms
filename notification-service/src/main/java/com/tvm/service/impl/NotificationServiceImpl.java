package com.tvm.service.impl;

import com.tvm.dto.NotificationRequestDTO;
import com.tvm.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendNotification(NotificationRequestDTO requestDTO) {
        try {
            Context context = new Context();
            context.setVariable("studentName", requestDTO.getStudentName());
            context.setVariable("courseName", requestDTO.getCourseName());
            context.setVariable("updateDetails", requestDTO.getUpdateDetails());
            context.setVariable("score", requestDTO.getScore());
            context.setVariable("offerTitle", requestDTO.getOfferTitle());
            context.setVariable("offerDetails", requestDTO.getOfferDetails());
            context.setVariable("expiryDate", requestDTO.getExpiryDate());

            String htmlContent = templateEngine.process(requestDTO.getTemplateName(), context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setTo(requestDTO.getToEmail());
            helper.setSubject(requestDTO.getSubject());
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send mail", e);
        }
    }

    public void sendTestEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kiruthikam421@gmail.com"); // must match spring.mail.username
        message.setTo(toEmail);
        message.setSubject("Test Email from Spring Boot");
        message.setText("This is a test email sent using Spring Boot and Gmail SMTP.");

        javaMailSender.send(message);
        System.out.println("Mail sent successfully to " + toEmail);
    }
}