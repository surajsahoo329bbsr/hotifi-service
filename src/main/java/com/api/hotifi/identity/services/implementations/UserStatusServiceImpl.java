package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.entities.UserStatus;
import com.api.hotifi.identity.errors.UserErrorMessages;
import com.api.hotifi.identity.model.EmailModel;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.repositories.UserStatusRepository;
import com.api.hotifi.identity.services.interfaces.IDeviceService;
import com.api.hotifi.identity.services.interfaces.IEmailService;
import com.api.hotifi.identity.services.interfaces.IUserStatusService;
import com.api.hotifi.identity.web.request.UserStatusRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserStatusServiceImpl implements IUserStatusService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IEmailService emailService;

    @Transactional
    @Override
    public List<UserStatus> addUserStatus(UserStatusRequest userStatusRequest) {
        try {
            //Request must be of user being warned or deleted
            boolean isUserBeingWarnedXorDeleted = userStatusRequest.getWarningReason() != null ^ userStatusRequest.getDeleteReason() != null;

            if (!isUserBeingWarnedXorDeleted)
                throw new Exception("Request must have either warning or deletion.");
            //Get user from id
            User user = userRepository.getOne(userStatusRequest.getUserId());
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);

            //Logic to see user if he/she has been freezed or banned
            if(authentication.isDeleted())
                throw new Exception("User already deleted");
            if(authentication.isBanned())
                throw new Exception("User already banned");
            if(authentication.isFreezed())
                throw new Exception("User already freezed");

            //If user is not banned or deleted or freezed
            Long userId = userStatusRequest.getUserId();
            List<UserStatus> userStatuses = getUserStatusByUserId(userId);
            UserStatus userStatus = new UserStatus();
            userStatus.setUser(user);
            userStatus.setRole(userStatusRequest.getRole());

            Date now = new Date(System.currentTimeMillis());

            if (userStatus.getDeletedReason() != null) {
                log.info("Inside delete");
                userStatus.setDeletedReason(userStatusRequest.getDeleteReason());
                userStatus.setDeletedAt(now);
                deleteUser(userId);
            } else {
                //else the warning reason is not null
                userStatus.setWarningReason(userStatusRequest.getWarningReason());
                userStatus.setWarningCreatedAt(now);

                if (userStatuses != null) {
                    if (userStatuses.size() == 10) {
                        //freeze
                        if (userStatusRequest.getFreezeReason() == null)
                            throw new Exception("Freeze Reason not present.");
                        userStatus.setFreezeReason(userStatusRequest.getFreezeReason());
                        userStatus.setFreezeCreatedAt(now);
                        userStatus.setFreezePeriod(24); //24 hours
                        freezeUser(userId, true);
                    } else if (userStatuses.size() == 15) {
                        //ban
                        if (userStatusRequest.getBanReason() == null)
                            throw new Exception("Warning Reason not present.");
                        userStatus.setBanReason(userStatusRequest.getBanReason());
                        userStatus.setBanCreatedAt(now);
                        banUser(userId, true);
                    }
                }
            }

            userStatusRepository.save(userStatus);
            return getUserStatusByUserId(userId);

        } catch (Exception e) {
            log.error("Error Occured : ", e);
        }
        return null;
    }

    @Transactional
    @Override
    public List<UserStatus> getUserStatusByUserId(Long userId) {
        try {
            return userStatusRepository.findUserStatusByUserId(userId);
        } catch (Exception e) {
            log.error("Error occured", e);
        }
        return null;
    }

    //for ban, deactivate, freeze and delete check user_status table for reasons to do so
    //UserDefined functions
    @Override
    @Transactional
    public void freezeUser(Long userId, boolean freezeUser) {
        try {
            User user = userRepository.getOne(userId);
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);

            //If we are unfreezing a freezed user
            if(!freezeUser){
                //Logic to activate user if he/she has been freezed or banned
                if(authentication.isDeleted())
                    throw new Exception("User already deleted");
                if(authentication.isBanned())
                    throw new Exception("User already banned");

                //Check if freezed user is activating
                if(authentication.isFreezed()){
                    List<UserStatus> userStatuses = getUserStatusByUserId(user.getId());
                    for(UserStatus userStatus : userStatuses){
                        if(userStatus.getFreezeReason() != null){
                            if(!isFreezePeriodExpired(userStatus)){
                                log.error("Freeze period not over yet.");
                                throw new Exception("Freeze period not over yet.");
                            }
                        }
                    }
                }
            }

            if (authentication.isFreezed() && freezeUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_FREEZED);
            } else if (!authentication.isFreezed() && !freezeUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_FREEZED);
            } else {
                authentication.setFreezed(freezeUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    public void banUser(Long userId, boolean banUser) {
        try {
            User user = userRepository.getOne(userId);
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);

            if (authentication.isBanned() && banUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_BANNED);
            } else if (!authentication.isBanned() && !banUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_BANNED);
            } else {
                if(banUser) {
                    user.setLoggedIn(false);
                    userRepository.save(user);
                }
                authentication.setBanned(banUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        try {
            User user = userRepository.getOne(userId);
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);

            if (authentication.isDeleted()) {
                log.error("Account already deleted");
                throw new Exception("Account already deleted");
            }

            //set authentication values to null
            authentication.setDeleted(true);
            authentication.setEmail(null);
            authentication.setPhone(null);
            authenticationRepository.save(authentication);

            //delete user devices
            deviceService.deleteUserDevices(userId);

            //set user values to null
            user.setFacebookId(null);
            user.setGoogleId(null);
            user.setLoggedIn(false);
            userRepository.save(user);

            EmailModel emailModel = new EmailModel();
            emailModel.setToEmail(authentication.getEmail());
            emailModel.setFromEmail(Constants.FROM_EMAIL);
            emailModel.setFromEmailPassword(Constants.FROM_EMAIL_PASSWORD);
            emailService.sendEmail(user, emailModel, 2);


        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    public boolean isFreezePeriodExpired(UserStatus userStatus){
        Date currentTime = new Date(System.currentTimeMillis());
        long timeDifference =  currentTime.getTime() - userStatus.getFreezeCreatedAt().getTime();
        long hoursDifference = timeDifference / (60L * 60L * 1000L);
        return hoursDifference >= userStatus.getFreezePeriod(); // If time period has exceeded freeze period
    }

}
