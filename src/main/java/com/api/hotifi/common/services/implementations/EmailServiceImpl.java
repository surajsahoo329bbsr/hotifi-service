package com.api.hotifi.common.services.implementations;


import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.common.processors.codes.CloudClientCodes;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.models.EmailModel;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final INotificationService notificationService;

    public EmailServiceImpl(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void sendWelcomeEmail(User user, EmailModel emailModel) {
        try {
            String subject = "Welcome To Hotifi";
            File file = new ClassPathResource(AppConfigurations.EMAIL_WELCOME_HTML_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi " + user.getFirstName() + ",");
            sendEmail(document, emailModel, subject);
            notificationService.sendNotification(user.getId(), "A New Beginning !", "Your hotifi account has been created.", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAccountDeletedEmail(User user, EmailModel emailModel) {
        try {
            String subject = "Your account has been deleted";
            File file = new ClassPathResource(AppConfigurations.EMAIL_ACCOUNT_DELETED_HTML_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi " + user.getFirstName() + ",");
            sendEmail(document, emailModel, subject);
            notificationService.sendNotification(user.getId(), "Sorry To See You Go !", "Your hotifi account has been deleted.", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAccountFreezedEmail(User user, EmailModel emailModel) {
        try {
            String subject = "Your account has been freezed";
            File file = new ClassPathResource(AppConfigurations.EMAIL_ACCOUNT_FREEZED_HTML_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi " + user.getFirstName() + ",");
            document.getElementById("freeze-msg").appendText(BusinessConfigurations.MINIMUM_FREEZE_PERIOD_HOURS + "hours");
            sendEmail(document, emailModel, subject);
            notificationService.sendNotification(user.getId(), "FREEEEEEZE THERE !", "Your hotifi account for buying data has been freeze.", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBuyerBannedEmail(User user, EmailModel emailModel) {
        try {
            String subject = "Your buying account has been banned";
            File file = new ClassPathResource(AppConfigurations.EMAIL_BUYER_BANNED_HTML_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi " + user.getFirstName() + ",");
            sendEmail(document, emailModel, subject);
            notificationService.sendNotification(user.getId(), "Banned !", "Your hotifi account for buying data has been deleted.", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmailOtpEmail(EmailModel emailModel) {
        try {
            String subject = "Email Otp Verification";
            File file = new ClassPathResource(AppConfigurations.EMAIL_OTP_HTML_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi,");
            document.getElementById("email-otp").appendText(emailModel.getEmailOtp());
            document.getElementById("expires-in").appendText(BusinessConfigurations.MAXIMUM_EMAIL_OTP_MINUTES + " minutes");
            sendEmail(document, emailModel, subject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLinkedAccountFailed(User user, String errorDescription, EmailModel emailModel) {
        try {
            String subject = "Your linked account verification for payment failed";
            File file = new ClassPathResource(AppConfigurations.EMAIL_LINKED_ACCOUNT_FAILED_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi " + user.getFirstName() + ",");
            document.getElementById("error-msg").appendText(errorDescription);
            sendEmail(document, emailModel, subject);
            notificationService.sendNotification(user.getId(), "Oh No! Oh No! Oh No No No No !", "Your linked account verification for payment has failed.", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLinkedAccountSuccessEmail(User user, EmailModel emailModel) {
        try {
            String subject = "Your linked account verification for payment is successful";
            File file = new ClassPathResource(AppConfigurations.EMAIL_LINKED_ACCOUNT_SUCCESS_PATH).getFile();
            Document document = Jsoup.parse(file, "UTF-8");
            document.getElementById("first-name").appendText("Hi " + user.getFirstName() + ",");
            sendEmail(document, emailModel, subject);
            notificationService.sendNotification(user.getId(), "Here We Go !", "Your linked account verification for payment is successful.", CloudClientCodes.GOOGLE_CLOUD_PLATFORM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(Document document, EmailModel emailModel, String subject) {
        String htmlContent = document.html();
        Email email = EmailBuilder.startingBlank()
                .from(emailModel.getFromEmail())
                .to(emailModel.getToEmail())
                .withSubject(subject)
                .withHTMLText(htmlContent)
                .buildEmail();
        Mailer mailer = MailerBuilder
                .withSMTPServer(AppConfigurations.EMAIL_HOST, AppConfigurations.EMAIL_PORT, emailModel.getFromEmail(), emailModel.getFromEmailPassword())
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(10 * 1000)
                .async()
                .buildMailer();
        mailer.sendMail(email);
        log.info("Email Sent");
    }

}
