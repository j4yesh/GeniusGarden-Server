package com.geniusgarden.server.Service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.name}")
    private String senderMail;

    public void sendmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(senderMail);
        helper.setTo(toEmail);
        helper.setSubject(subject);

        helper.setText(body, true);

        mailSender.send(mimeMessage);
        System.out.println("Mail sent successfully to " + toEmail);
    }


    public static String generateOtp(int length) {
            String numbers = "0123456789";

            SecureRandom secureRandom = new SecureRandom();

            StringBuilder otp = new StringBuilder(length);

            for (int i = 0; i < length; i++) {
                otp.append(numbers.charAt(secureRandom.nextInt(numbers.length())));
            }

            return otp.toString();
        }

}
