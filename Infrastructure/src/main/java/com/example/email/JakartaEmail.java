package com.example.email;


import com.example.exceptions.EmailException;
import com.example.login.User;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Properties;

@Component
public class JakartaEmail implements EmailPort {
    //TODO - move credentials to application.properties
    private final Properties prop = new Properties();
    private String username = "api";
    private String password = "a03d177495c203b303d33e860605fc95";

    JakartaEmail() {

        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host",
                "live.smtp.mailtrap.io");
        prop.put("mail.smtp.port", "587");
    }

    @Override
    public void sendEmail(String path, String email, String firstName, String lastName) throws EmailException {
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@docuflow.social"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Document Generation");

            String msg = "<p class=\"isSelectedEnd\">Dear " + firstName + " " + lastName + ",</p>\n" +
                    "<p class=\"isSelectedEnd\">The requested document got all approvals and has been securely generated and is attached to this email. The content of the attached file may contain confidential and/or personal information and is intended solely for the designated recipient.</p>\n" +
                    "<p class=\"isSelectedEnd\">Unauthorized access, use, disclosure, distribution, or copying of this document or its contents is strictly prohibited. If you have received this email in error, please delete it immediately.</p>\n" +
                    "<p class=\"isSelectedEnd\">Thank you for your cooperation.</p>\n" +
                    "<p>Sincerely,<br><strong>DocuFlow &ndash; Automated Document Delivery</strong><br><a>documents@docuflow.social</a></p>\n" +
                    "<p><strong><span style=\"color: #e03e2d;\"><em>This is an automated message. Please do not reply.</em></span></strong></p>";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            File file = new File(path);
            attachmentBodyPart.attachFile(file);


            multipart.addBodyPart(mimeBodyPart);
            multipart.addBodyPart(attachmentBodyPart);
            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e) {
            throw new EmailException(e.getMessage());
        }
    }
    public void sendRegisterEmail(User user){
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@docuflow.social"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject("Welcome to DocuFlow!");

            String msg = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>\n" +
                    "<p><strong>Welcome to DocuFlow!</strong> Your account has been successfully created. You can now access our platform using the credentials below:</p>\n" +
                    "\n" +
                    "<div style=\"background-color: #f4f4f4; padding: 15px; border-radius: 5px; border: 1px solid #ddd; margin: 20px 0;\">\n" +
                    "    <p style=\"margin: 0;\"><strong>Username:</strong> " + user.getUsername() + "</p>\n" +
                    "    <p style=\"margin: 5px 0 0 0;\"><strong>Password:</strong> " + user.getPassword() + "</p>\n" +
                    "</div>\n" +

                    "<p>For your security, we recommend that you <strong>change your password</strong> immediately after your first login from the account settings menu.</p>\n" +
                    "<p>Sincerely,<br><strong>The DocuFlow Team</strong><br><a href=\"mailto:support@docuflow.social\">support@docuflow.social</a></p>\n" +
                    "<p><strong><span style=\"color: #e03e2d;\"><em>This is an automated message. Please do not reply.</em></span></strong></p>";

            message.setContent(msg, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            throw new EmailException(e.getMessage());
        }
    }


}
