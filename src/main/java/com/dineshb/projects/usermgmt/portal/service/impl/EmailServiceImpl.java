package com.dineshb.projects.usermgmt.portal.service.impl;

import com.dineshb.projects.usermgmt.portal.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.dineshb.projects.usermgmt.portal.constants.EmailConstants.EMAIL_SUBJECT;
import static com.dineshb.projects.usermgmt.portal.constants.EmailConstants.FROM_EMAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendUserRegistrationEmail(String firstName, String lastName,
                                          String username, String email, String password) {

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(FROM_EMAIL);
        emailMessage.setTo(email);
        emailMessage.setSubject(EMAIL_SUBJECT);
        emailMessage.setText(getRegistrationEmailText(firstName, lastName, username, password));
        emailMessage.setSentDate(new Date());
        javaMailSender.send(emailMessage);
    }

    @Override
    public void sendPasswordResetEmail(String firstName, String lastName, String username, String email, String password) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(FROM_EMAIL);
        emailMessage.setTo(email);
        emailMessage.setSubject(EMAIL_SUBJECT);
        emailMessage.setText(getPasswordResetEmailText(firstName, lastName, username, password));
        emailMessage.setSentDate(new Date());
        javaMailSender.send(emailMessage);
    }


    private String getRegistrationEmailText(final String firstName, final String lastName,
                                            final String username, final String password) {
        return "Dear " + firstName + " " + lastName + "," + "\n" +
                "You have been registered to the User Management Portal. \n" +
                "Below are your credentials to login. \n" +
                "Username : " + username + "\n" +
                "Password : " + password + "\n" +
                "\n" +
                "\n" +
                "Regards," +
                "Team JBSoft Inc.";
    }

    private String getPasswordResetEmailText(final String firstName, final String lastName,
                                             final String username, final String password) {
        return "Dear " + firstName + " " + lastName + "," + "\n" +
                "Your Password has been changed !! \n" +
                "Below are your new credentials to login. \n" +
                "Username : " + username + "\n" +
                "Password : " + password + "\n" +
                "\n" +
                "\n" +
                "Regards," +
                "Team JBSoft Inc.";
    }
}
