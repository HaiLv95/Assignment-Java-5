package com.assignmentjava.services;

import com.assignmentjava.model.Mail;

import javax.mail.MessagingException;

public interface SupportServices {

    void sendEmail(Mail mail) throws MessagingException;
}
