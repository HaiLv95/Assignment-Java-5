package com.assignmentjava.config;

import com.assignmentjava.services.DotEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

//Config đối tượng thuộc tính

@Configuration
public class MailConfig {

    @Autowired
    DotEnvService dotEnvService;

   @Bean("mail")
    public JavaMailSender javaMailSender(){
       JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
       mailSender.setPort(Integer.parseInt(dotEnvService.getDotEnvValue("MAIL_PORT")));
       mailSender.setHost(dotEnvService.getDotEnvValue("MAIL_HOST"));
       mailSender.setUsername(dotEnvService.getDotEnvValue("MAIL_USERNAME"));
       mailSender.setPassword(dotEnvService.getDotEnvValue("MAIL_PASSWORD"));
       Properties props = new Properties();
       props.put("mail.smpt.auth", "true");
       props.put("mail.smtp.starttls.enable", "true");
       props.put("mail.default-encoding", "UTF-8");
       props.put("mail.transport.protocol", "smtp");
       props.put("mail.smtp.auth", "true");

       //show debug sendemail
//       props.put("mail.debug", "true");
       mailSender.setJavaMailProperties(props);
       return mailSender;
   }
}
