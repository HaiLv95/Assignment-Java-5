package com.assignmentjava.model;

public class Mail {
    private  String subject;
    private String content;
    private String sendTo;
    private String mailFrom;

    public Mail() {
    }

    public Mail(String subject, String content, String sendTo, String mailFrom) {
        this.subject = subject;
        this.content = content;
        this.sendTo = sendTo;
        this.mailFrom = mailFrom;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }
}
