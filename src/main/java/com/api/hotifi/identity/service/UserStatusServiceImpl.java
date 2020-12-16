package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.entity.UserStatus;
import com.api.hotifi.identity.error.UserErrorMessages;
import com.api.hotifi.identity.repository.AuthenticationRepository;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.identity.repository.UserStatusRepository;
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

    @Transactional
    @Override
    public List<UserStatus> addUserStatus(UserStatusRequest userStatusRequest) {
        try {
            //Request must be of user being warned or deleted
            boolean isUserBeingWarnedXorDeleted = userStatusRequest.getWarningReason() != null ^ userStatusRequest.getDeleteReason() != null;

            if (!isUserBeingWarnedXorDeleted)
                throw new Exception("Request must have either warning or deletion.");

            Long userId = userStatusRequest.getUserId();
            List<UserStatus> userStatuses = getUserStatusByUserId(userId);
            UserStatus userStatus = new UserStatus();

            Date now = new Date(System.currentTimeMillis());
            User user = userRepository.getOne(userStatusRequest.getUserId());
            userStatus.setUser(user);
            userStatus.setRole(userStatusRequest.getRole());

            //incomplete logic to be completed...
            if (userStatus.getDeletedReason() != null) {
                userStatusRepository.save(userStatus);
                deleteUser(userId);
            } else if(userStatuses == null || userStatuses.size() < 4) {
                userStatus.setWarningReason(userStatusRequest.getWarningReason());

            } else if (userStatuses.size() < 7){
                //freeze
                //add logic
            } else if (userStatuses.size() < 9) {
                //ban
                //add logic
            }

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
    //write logic in below methods for that
    //It is understood that user has been created if both email and

    @Transactional
    @Override
    public void activateUser(Long id, boolean activateUser) {
        try {
            Authentication authentication = authenticationRepository.getOne(id);

            //Write logic for why to deactivate / activate user checking from user_status table

            if (authentication.isActivated() && activateUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_ACTIVATED);
            } else if (!authentication.isActivated() && !activateUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_ACTIVATED);
            } else {
                authentication.setActivated(activateUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void freezeUser(Long id, boolean freezeUser) {
        try {
            Authentication authentication = authenticationRepository.getOne(id);

            //Write logic for why to deactivate / activate user checking from user_status table

            if (authentication.isFreezed() && freezeUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_FREEZED);
            } else if (!authentication.isFreezed() && !freezeUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_FREEZED);
            } else {
                authentication.setActivated(freezeUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void banUser(Long id, boolean banUser) {
        try {
            Authentication authentication = authenticationRepository.getOne(id);

            //Write logic for why to deactivate / activate user checking from user_status table

            if (authentication.isBanned() && banUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_BANNED);
            } else if (!authentication.isBanned() && !banUser) {
                throw new Exception(UserErrorMessages.USER_ALREADY_NOT_BANNED);
            } else {
                authentication.setActivated(banUser);
                authenticationRepository.save(authentication);
            }
        } catch (Exception e) {
            log.error("Error Message ", e);
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        try {
            User user = userRepository.getOne(id);
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
            deviceService.deleteUserDevices(id);

            //set user values to null
            user.setFacebookId(null);
            user.setGoogleId(null);
            user.setLoggedIn(false);
            userRepository.save(user);

        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    /*public void setUserStatus(UserStatus userStatus, UserStatusRequest userStatusRequest) throws Exception {

        if (userStatus == null)
            throw new Exception("User doesn't exist");
        Date now = new Date(System.currentTimeMillis());
        User user = userRepository.getOne(userStatusRequest.getUserId());
        userStatus.setUser(user);
        userStatus.setWarningReason(userStatusRequest.getWarningReason());
        userStatus.setRole(userStatusRequest.getRole());

        boolean isAccountBeingLocked
                = userStatusRequest.getFreezeReason() != null
                || userStatusRequest.getBanReason() != null
                || userStatusRequest.getDeleteReason() != null;

        //if(userS)

        if (userStatusRequest.getFreezeReason() != null) {

        } else if (userStatusRequest.getBanReason() != null) {

        } else {

        }
    }*/
}
