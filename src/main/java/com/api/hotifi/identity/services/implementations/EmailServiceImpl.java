package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.models.EmailModel;
import com.api.hotifi.identity.services.interfaces.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.io.File;

@Slf4j
public class EmailServiceImpl implements IEmailService {

    //TODO service configuration

    public EmailServiceImpl(){

    }

    public void sendEmail(User user, EmailModel emailModel, int emailService){
        try {

            String subject;
            File file;
            Document document;
            /*switch (emailService){
                case 0:
                    file = new File(Constants.EMAIL_OTP_HTML_PATH);
                    document = Jsoup.parse(file, "UTF-8");
                    // set doc's attributes
                    document.getElementById("email_otp").appendText("Your email otp is " + emailModel.getEmailOtp());
                    subject = "Email Otp Verification";
                    break;
                case 1:
                    file = new File(Constants.EMAIL_WELCOME_HTML_PATH);
                    document = Jsoup.parse(file, "UTF-8");
                    // set doc's attributes
                    document.getElementById("user_first_name").appendText("Hi, " + user.getFirstName());
                    subject = "Warm welcome to Hotifi !";
                    break;
                default:
                    file = new File(Constants.EMAIL_GOODBYE_HTML_PATH); // Put your Html File Path here
                    document = Jsoup.parse(file, "UTF-8");
                    subject = "Hard to see you go !"; // Put your subject here
                    break;
            }

            String htmlContent = document.html();

            Email email = EmailBuilder.startingBlank()
                    .from(emailModel.getFromEmail())
                    .to(emailModel.getToEmail())
                    .withSubject(subject)
                    .withHTMLText(htmlContent)
                    .buildEmail();

            Mailer mailer = MailerBuilder
                    .withSMTPServer(Constants.EMAIL_HOST, Constants.EMAIL_PORT, emailModel.getFromEmail(), emailModel.getFromEmailPassword())
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .withSessionTimeout(10 * 1000)
                    .async()
                    .buildMailer();

            mailer.sendMail(email);*/
            log.info("Email Sent");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
