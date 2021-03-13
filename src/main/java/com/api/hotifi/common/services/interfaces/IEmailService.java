package com.api.hotifi.common.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.models.EmailModel;

public interface IEmailService {


    void sendAccountDeletedEmail(User user, EmailModel emailModel);

    void sendAccountFreezedEmail(User user, EmailModel emailModel);

    void sendBuyerBannedEmail(User user, EmailModel emailModel);

    void sendEmailOtpEmail(EmailModel emailModel);

    void sendLinkedAccountFailed(User user, String errorDescription, EmailModel emailModel);

    void sendLinkedAccountSuccessEmail(User user, EmailModel emailModel);

    void sendWelcomeEmail(User user, EmailModel emailModel);


}
