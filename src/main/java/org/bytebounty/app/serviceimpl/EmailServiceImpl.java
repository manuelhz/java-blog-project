package org.bytebounty.app.serviceimpl;

import org.bytebounty.app.config.ReadFile;
import org.bytebounty.app.model.Email;
import org.bytebounty.app.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;






@Service("emailService")
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

   
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public boolean sendSimpleEmail(Email email) {
        try {
            

            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            mailMessage.setFrom(new InternetAddress(ReadFile.readFile(sender)));
            mailMessage.setRecipients(MimeMessage.RecipientType.TO, email.getRecipient());
            mailMessage.setContent(email.getMsgBody(), "text/html; charset=utf-8");
            mailMessage.setSubject(email.getSubject());

            javaMailSender.send(mailMessage);

        return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }
    
}
