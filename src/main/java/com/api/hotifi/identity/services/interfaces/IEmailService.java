package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.models.EmailModel;

public interface IEmailService {

    void sendEmail(User user, EmailModel emailModel, int emailService);

}
