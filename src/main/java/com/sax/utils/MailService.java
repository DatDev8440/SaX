package com.sax.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.security.SecureRandom;
import java.util.Properties;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.*;

public class MailService {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(randomIndex));
        }

        return randomString.toString();
    }

    public static void sendEmail(String email) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");


        javax.mail.Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("dungbhph35753@fpt.edu.vn", "hjda gxxc hluu sfzz");
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("dungbhph35753@fpt.edu.vn"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Lấy mã xác nhận lấy lại mật");
        String otp = generateRandomString();
        com.sax.utils.Session.otp = otp;
        message.setText("Mã xác nhân là: " + otp);
        Transport.send(message);
    }
        public static boolean isValidEmail(String email) {
            try {
                InternetAddress internetAddress = new InternetAddress(email);
                internetAddress.validate();
                return true;
            } catch (AddressException e) {
                return false;
            }
        }

}
