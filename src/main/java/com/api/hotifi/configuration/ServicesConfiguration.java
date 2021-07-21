package com.api.hotifi.configuration;

import com.api.hotifi.authorization.jwt.JwtDecoder;
import com.api.hotifi.authorization.service.CustomerAuthorizationImpl;
import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.common.services.implementations.EmailServiceImpl;
import com.api.hotifi.common.services.implementations.NotificationServiceImpl;
import com.api.hotifi.common.services.implementations.VerificationServiceImpl;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.services.interfaces.IFirebaseMessagingService;
import com.api.hotifi.common.services.interfaces.INotificationService;
import com.api.hotifi.common.services.interfaces.IVerificationService;
import com.api.hotifi.identity.repositories.*;
import com.api.hotifi.identity.services.implementations.AuthenticationServiceImpl;
import com.api.hotifi.identity.services.implementations.DeviceServiceImpl;
import com.api.hotifi.identity.services.implementations.UserServiceImpl;
import com.api.hotifi.identity.services.implementations.UserStatusServiceImpl;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.services.interfaces.IUserStatusService;
import com.api.hotifi.payment.processor.PaymentProcessor;
import com.api.hotifi.payment.repositories.*;
import com.api.hotifi.payment.services.implementations.*;
import com.api.hotifi.payment.services.interfaces.*;
import com.api.hotifi.session.repository.SessionRepository;
import com.api.hotifi.session.service.ISessionService;
import com.api.hotifi.session.service.SessionServiceImpl;
import com.api.hotifi.speedtest.repository.SpeedTestRepository;
import com.api.hotifi.speedtest.service.ISpeedTestService;
import com.api.hotifi.speedtest.service.SpeedTestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfiguration {

    @Bean
    public ICustomerAuthorizationService customerAuthorizationService(AuthenticationRepository authenticationRepository, UserRepository userRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, SellerReceiptRepository sellerReceiptRepository, DeviceRepository deviceRepository, JwtDecoder jwtDecoder) {
        return new CustomerAuthorizationImpl(authenticationRepository, userRepository, sessionRepository, purchaseRepository, sellerReceiptRepository, deviceRepository, jwtDecoder);
    }

    @Bean
    public IAuthenticationService authenticationService(AuthenticationRepository authenticationRepository, RoleRepository roleRepository, IEmailService emailService, IVerificationService verificationService) {
        return new AuthenticationServiceImpl(authenticationRepository, roleRepository, emailService, verificationService);
    }

    @Bean
    public IDeviceService deviceService(UserRepository userRepository, DeviceRepository deviceRepository) {
        return new DeviceServiceImpl(userRepository, deviceRepository);
    }

    @Bean
    public IEmailService emailService(INotificationService notificationService) {
        return new EmailServiceImpl(notificationService);
    }

    @Bean
    public INotificationService notificationService(DeviceRepository deviceRepository, IDeviceService deviceService, UserRepository userRepository, IFirebaseMessagingService firebaseMessagingService) {
        return new NotificationServiceImpl(deviceRepository, deviceService, userRepository,firebaseMessagingService);
    }

    @Bean
    public IVerificationService verificationService() {
        return new VerificationServiceImpl();
    }

    @Bean
    public IUserService userService(UserRepository userRepository, AuthenticationRepository authenticationRepository, IVerificationService verificationService, IEmailService emailService) {
        return new UserServiceImpl(userRepository, authenticationRepository, emailService, verificationService);
    }

    @Bean
    public IUserStatusService userStatusService(AuthenticationRepository authenticationRepository, UserStatusRepository userStatusRepository, UserRepository userRepository, BankAccountRepository bankAccountRepository, IDeviceService deviceService, IEmailService emailService) {
        return new UserStatusServiceImpl(authenticationRepository, userStatusRepository, userRepository, bankAccountRepository, deviceService, emailService);
    }

    @Bean
    public IFeedbackService feedbackService(UserRepository userRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, FeedbackRepository feedbackRepository) {
        return new FeedbackServiceImpl(userRepository, sessionRepository, purchaseRepository, feedbackRepository);
    }

    @Bean
    public IPurchaseService purchaseService(UserRepository userRepository, SpeedTestRepository speedTestRepository, SessionRepository sessionRepository, PurchaseRepository purchaseRepository, SellerPaymentRepository sellerPaymentRepository, IPaymentService sellerPaymentService) {
        return new PurchaseServiceImpl(userRepository, speedTestRepository, sessionRepository, purchaseRepository, sellerPaymentRepository, sellerPaymentService);
    }

    @Bean
    public IBankAccountService bankAccountService(UserRepository userRepository, BankAccountRepository bankAccountRepository, IEmailService emailService) {
        return new BankAccountServiceImpl(userRepository, bankAccountRepository, emailService);
    }

    @Bean
    public IPaymentService paymentService(SellerPaymentRepository sellerPaymentRepository, SellerReceiptRepository sellerReceiptRepository, UserRepository userRepository, PurchaseRepository purchaseRepository) {
        return new PaymentServiceImpl(sellerPaymentRepository, sellerReceiptRepository, userRepository, purchaseRepository);
    }

    @Bean
    public IStatsService statsService(UserRepository userRepository, PurchaseRepository purchaseRepository, SessionRepository sessionRepository, SellerPaymentRepository sellerPaymentRepository) {
        return new StatsServiceImpl(userRepository, purchaseRepository, sessionRepository, sellerPaymentRepository);
    }

    @Bean
    public ISessionService sessionService(UserRepository userRepository, SpeedTestRepository speedTestRepository, SessionRepository sessionRepository, SellerPaymentRepository sellerPaymentRepository, PurchaseRepository purchaseRepository, ISpeedTestService speedTestService, IFeedbackService feedbackService, IUserStatusService userStatusService, INotificationService notificationService) {
        return new SessionServiceImpl(userRepository, speedTestRepository, sessionRepository, sellerPaymentRepository, purchaseRepository, speedTestService, feedbackService, userStatusService, notificationService);
    }

    @Bean
    public ISpeedTestService speedTestService(UserRepository userRepository, SpeedTestRepository speedTestRepository) {
        return new SpeedTestServiceImpl(userRepository, speedTestRepository);
    }

}
