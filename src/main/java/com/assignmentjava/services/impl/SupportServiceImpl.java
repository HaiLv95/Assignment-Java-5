package com.assignmentjava.services.impl;

import com.assignmentjava.model.Mail;
import com.assignmentjava.services.SupportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
public class SupportServiceImpl implements SupportServices {


    @Autowired
    JavaMailSender mailSender;

    @Override
    public void sendEmail(Mail mail) throws MessagingException {
        // Tạo message
        MimeMessage message = mailSender.createMimeMessage();
        // Sử dụng Helper để thiết lập các thông tin cần thiết cho message
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom(mail.getMailFrom());
        helper.setTo(mail.getSendTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getContent(), true);
        helper.setReplyTo(mail.getMailFrom());
        // Gửi message đến SMTP server
        mailSender.send(message);
    }
}
