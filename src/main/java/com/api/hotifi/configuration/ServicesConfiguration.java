package com.api.hotifi.configuration;

import com.api.hotifi.common.services.implementations.EmailServiceImpl;
import com.api.hotifi.common.services.implementations.NotificationServiceImpl;
import com.api.hotifi.common.services.implementations.SocialServiceImpl;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.common.services.interfaces.ISocialService;
import com.api.hotifi.identity.repositories.*;
import com.api.hotifi.identity.services.implementations.*;
import com.api.hotifi.identity.services.interfaces.*;
import com.api.hotifi.payment.repositories.*;
import com.api.hotifi.payment.services.implementations.*;
import com.api.hotifi.payment.services.interfaces.*;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.session.service.ISessionService;
import com.api.hotifi.session.service.SessionServiceImpl;
import com.api.hotifi.speed_test.repository.SpeedTestRepository;
import com.api.hotifi.speed_test.service.ISpeedTestService;
import com.api.hotifi.speed_test.service.SpeedTestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfiguration {

    @Bean
    public IAuthenticationService authenticationService(AuthenticationRepository authenticationRepository, RoleRepository roleRepository, IEmailService emailService) {
        return new AuthenticationServiceImpl(authenticationRepository, roleRepository, emailService);
    }

    @Bean
    public IDeviceService deviceService(UserRepository userRepository, DeviceRepository deviceRepository) {
        return new DeviceServiceImpl(userRepository, deviceRepository);
    }

    @Bean
    public IEmailService emailService(){
        return new EmailServiceImpl();
    }

    @Bean
    public INotificationService notificationService(IDeviceService deviceService){
        return new NotificationServiceImpl(deviceService);
    }

    @Bean
    public ISocialService socialService(){
        return new SocialServiceImpl();
    }

    @Bean
    public IUserService userService(UserRepository userRepository, AuthenticationRepository authenticationRepository, IEmailService emailService) {
        return new UserServiceImpl(userRepository, authenticationRepository, emailService);
    }

    @Bean
    public IUserStatusService userStatusService(AuthenticationRepository authenticationRepository, UserStatusRepository userStatusRepository, UserRepository userRepository, BankAccountRepository bankAccountRepository, IDeviceService deviceService, IEmailService emailService){
        return new UserStatusServiceImpl(authenticationRepository, userStatusRepository, userRepository, bankAccountRepository, deviceService, emailService);
    }

    @Bean
    public IFeedbackService feedbackService(UserRepository userRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, FeedbackRepository feedbackRepository){
        return new FeedbackServiceImpl(userRepository, sessionRepository, purchaseRepository, feedbackRepository);
    }

    @Bean
    public IPurchaseService purchaseService(UserRepository userRepository, SpeedTestRepository speedTestRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, SellerPaymentRepository sellerPaymentRepository, ISellerPaymentService sellerPaymentService){
        return new PurchaseServiceImpl(userRepository, speedTestRepository, sessionRepository, purchaseRepository, sellerPaymentRepository, sellerPaymentService);
    }

    @Bean
    public IBankAccountService sellerBankAccountService(UserRepository userRepository, BankAccountRepository bankAccountRepository){
        return new BankAccountServiceImpl(userRepository, bankAccountRepository);
    }

    @Bean
    public ISellerPaymentService sellerPaymentService(SellerPaymentRepository sellerPaymentRepository, SellerReceiptRepository sellerReceiptRepository, UserRepository userRepository, ISellerReceiptService sellerReceiptService){
        return new SellerPaymentServiceImpl(sellerPaymentRepository, sellerReceiptRepository, userRepository, sellerReceiptService);
    }

    @Bean
    public ISellerReceiptService sellerReceiptServiceSellerReceiptServiceImpl(SellerReceiptRepository sellerReceiptRepository, SellerPaymentRepository sellerPaymentRepository){
        return new SellerReceiptServiceImpl(sellerReceiptRepository, sellerPaymentRepository);
    }

    @Bean
    public IStatsService statsService(UserRepository userRepository, PurchaseRepository purchaseRepository, SessionRepository sessionRepository, SellerPaymentRepository sellerPaymentRepository, IFeedbackService feedbackService){
        return new StatsServiceImpl(userRepository, purchaseRepository, sessionRepository, sellerPaymentRepository, feedbackService);
    }

    @Bean
    public ISessionService sessionService(UserRepository userRepository, SpeedTestRepository speedTestRepository, ISpeedTestService speedTestService, SessionRepository sessionRepository, SellerPaymentRepository sellerPaymentRepository, IFeedbackService feedbackService, PurchaseRepository purchaseRepository){
        return new SessionServiceImpl(userRepository, speedTestRepository, speedTestService, sessionRepository, sellerPaymentRepository, feedbackService, purchaseRepository);
    }

    @Bean
    public ISpeedTestService speedTestService(UserRepository userRepository, SpeedTestRepository speedTestRepository){
        return new SpeedTestServiceImpl(userRepository, speedTestRepository);
    }
}
