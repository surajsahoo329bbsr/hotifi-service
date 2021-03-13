package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.entities.UserStatus;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.errors.UserStatusErrorCodes;
import com.api.hotifi.identity.models.EmailModel;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.repositories.UserStatusRepository;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.api.hotifi.identity.services.interfaces.IUserStatusService;
import com.api.hotifi.identity.web.request.UserStatusRequest;
import com.api.hotifi.payment.repositories.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserStatusServiceImpl implements IUserStatusService {

    private final AuthenticationRepository authenticationRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final IDeviceService deviceService;
    private final IEmailService emailService;

    public UserStatusServiceImpl(AuthenticationRepository authenticationRepository, UserStatusRepository userStatusRepository, UserRepository userRepository, BankAccountRepository bankAccountRepository, IDeviceService deviceService, IEmailService emailService) {
        this.authenticationRepository = authenticationRepository;
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.deviceService = deviceService;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public List<UserStatus> addUserStatus(UserStatusRequest userStatusRequest) {


        //Request must be of user being warned or deleted
        boolean isUserBeingWarnedXorDeleted = userStatusRequest.getWarningReason() != null ^ userStatusRequest.getDeleteReason() != null;

        if (!isUserBeingWarnedXorDeleted)
            throw new HotifiException(UserStatusErrorCodes.NEITHER_WARNING_NOR_DELETE_REASON);
        //Get user from id
        User user = userRepository.findById(userStatusRequest.getUserId()).orElse(null);
        Long authenticationId = user != null ? user.getAuthentication().getId() : null;
        Authentication authentication = authenticationId != null ? authenticationRepository.findById(authenticationId).orElse(null) : null;

        if (authentication == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        //Logic to see user if he/she has been freezed or banned
        if (authentication.isDeleted())
            throw new HotifiException(UserErrorCodes.USER_ALREADY_DELETED);
        if (authentication.isBanned())
            throw new HotifiException(UserErrorCodes.USER_ALREADY_BANNED);
        if (authentication.isFreezed())
            throw new HotifiException(UserErrorCodes.USER_ALREADY_FREEZED);

        //If user is not banned or deleted or freezed
        Long userId = userStatusRequest.getUserId();
        List<UserStatus> userStatuses = getUserStatusByUserId(userId);
        UserStatus userStatus = new UserStatus();
        userStatus.setUser(user);
        userStatus.setRole(userStatusRequest.getRole());

        Date now = new Date(System.currentTimeMillis());

        if (userStatus.getDeletedReason() != null) {
            userStatus.setDeletedReason(userStatusRequest.getDeleteReason());
            userStatus.setDeletedAt(now);
            deleteUser(userId);
        } else {
            //else the warning reason is not null
            userStatus.setWarningReason(userStatusRequest.getWarningReason());
            userStatus.setWarningCreatedAt(now);

            if (userStatuses != null) {
                if (userStatuses.size() == Constants.MINIMUM_WARNINGS_TO_FREEZE - 1) {
                    //freeze
                    if (userStatusRequest.getFreezeReason() == null)
                        throw new HotifiException(UserStatusErrorCodes.USER_FREEZE_REASON_ABSENT);
                    userStatus.setFreezeReason(userStatusRequest.getFreezeReason());
                    userStatus.setFreezeCreatedAt(now);
                    userStatus.setFreezePeriod(Constants.MINIMUM_FREEZE_PERIOD_HOURS); //24 hours
                    freezeUser(userId, true);
                } else if (userStatuses.size() == Constants.MINIMUM_WARNINGS_TO_BAN - 1) {
                    //ban
                    if (userStatusRequest.getBanReason() == null)
                        throw new HotifiException(UserStatusErrorCodes.USER_BAN_REASON_ABSENT);
                    userStatus.setBanReason(userStatusRequest.getBanReason());
                    userStatus.setBanCreatedAt(now);
                    banUser(userId, true);
                }
            }
        }

        userStatusRepository.save(userStatus);
        return getUserStatusByUserId(userId);
    }

    @Transactional
    @Override
    public List<UserStatus> getUserStatusByUserId(Long userId) {
        return userStatusRepository.findUserStatusByUserId(userId);
    }

    //for ban, deactivate, freeze and delete check user_status table for reasons to do so
    //UserDefined functions
    @Override
    @Transactional
    public void freezeUser(Long userId, boolean freezeUser) {
        User user = userRepository.findById(userId).orElse(null);
        Long authenticationId = user != null ? user.getAuthentication().getId() : null;
        Authentication authentication = authenticationId != null ? authenticationRepository.findById(authenticationId).orElse(null) : null;
        if (authentication == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        //If we are unfreezing a freezed user
        if (!freezeUser) {
            //Logic to activate user if he/she has been freezed or banned
            if (authentication.isDeleted())
                throw new HotifiException(UserErrorCodes.USER_ALREADY_DELETED);
            if (authentication.isBanned())
                throw new HotifiException(UserErrorCodes.USER_ALREADY_BANNED);
            //Check if freezed user is activating
            if (authentication.isFreezed()) {

                List<UserStatus> userStatuses = getUserStatusByUserId(user.getId())
                        .stream()
                        .filter(userStatus -> userStatus.getFreezeReason() != null)
                        .collect(Collectors.toList());

                userStatuses.forEach(userStatus -> {
                    if (!isFreezePeriodExpired(userStatus)) {
                        log.error("Freeze period not over yet.");
                        throw new HotifiException(UserStatusErrorCodes.USER_FREEZE_PERIOD_ACTIVE);
                    }
                });
            }
        }

        if (authentication.isFreezed() && freezeUser) {
            throw new HotifiException(UserErrorCodes.USER_ALREADY_FREEZED);
        } else if (!authentication.isFreezed() && !freezeUser) {
            throw new HotifiException(UserErrorCodes.USER_ALREADY_NOT_FREEZED);
        } else {
            authentication.setFreezed(freezeUser);
            authenticationRepository.save(authentication);
            if (freezeUser) {
                EmailModel emailModel = new EmailModel();
                emailModel.setToEmail(user.getAuthentication().getEmail());
                emailModel.setFromEmail(Constants.FROM_EMAIL);
                emailModel.setFromEmailPassword(Constants.FROM_EMAIL_PASSWORD);
                emailService.sendAccountFreezedEmail(user, emailModel);
            }
        }
    }

    @Transactional
    public void banUser(Long userId, boolean banUser) {
        User user = userRepository.findById(userId).orElse(null);
        Long authenticationId = user != null ? user.getAuthentication().getId() : null;
        Authentication authentication = authenticationId != null ? authenticationRepository.findById(authenticationId).orElse(null) : null;

        if ((authentication != null && authentication.isBanned()) && banUser)
            throw new HotifiException(UserErrorCodes.USER_ALREADY_BANNED);

        if (!(authentication != null && authentication.isBanned()) && !banUser)
            throw new HotifiException(UserErrorCodes.USER_ALREADY_NOT_BANNED);

        if (user != null && authentication != null) {
            if (banUser) {
                user.setLoggedIn(false);
                userRepository.save(user);
                EmailModel emailModel = new EmailModel();
                emailModel.setToEmail(user.getAuthentication().getEmail());
                emailModel.setFromEmail(Constants.FROM_EMAIL);
                emailModel.setFromEmailPassword(Constants.FROM_EMAIL_PASSWORD);
                emailService.sendBuyerBannedEmail(user, emailModel);
            }
            authentication.setBanned(banUser);
            authenticationRepository.save(authentication);
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        Long authenticationId = user != null ? user.getAuthentication().getId() : null;
        Authentication authentication = authenticationId != null ? authenticationRepository.findById(authenticationId).orElse(null) : null;
        if (authentication == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);

        if (authentication.isDeleted()) {
            log.error("Account already deleted");
            throw new HotifiException(UserErrorCodes.USER_ALREADY_DELETED);
        }
        //set authentication values to null
        authentication.setDeleted(true);
        authentication.setEmail(null);
        authentication.setPhone(null);
        authenticationRepository.save(authentication);

        //delete user devices
        deviceService.deleteUserDevices(userId);

        //delete linked bank account
        bankAccountRepository.delete(user.getBankAccount());

        //set user values to null
        user.setFacebookId(null);
        user.setGoogleId(null);
        user.setLoggedIn(false);
        userRepository.save(user);

        EmailModel emailModel = new EmailModel();
        emailModel.setToEmail(authentication.getEmail());
        emailModel.setFromEmail(Constants.FROM_EMAIL);
        emailModel.setFromEmailPassword(Constants.FROM_EMAIL_PASSWORD);
        emailService.sendAccountDeletedEmail(user, emailModel);
    }

    public boolean isFreezePeriodExpired(UserStatus userStatus) {
        Date currentTime = new Date(System.currentTimeMillis());
        long timeDifference = currentTime.getTime() - userStatus.getFreezeCreatedAt().getTime();
        long hoursDifference = timeDifference / (60L * 60L * 1000L);
        return hoursDifference >= userStatus.getFreezePeriod(); // If time period has exceeded freeze period
    }

}
