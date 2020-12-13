package com.api.hotifi.identity.service;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.exception.error.ErrorCode;
import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.repository.AuthenticationRepository;
import com.api.hotifi.identity.repository.UserRepository;
import com.api.hotifi.identity.web.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Override
    @Transactional
    public void addUser(UserRequest userRequest) {
        try {
            Authentication authentication = authenticationRepository.getOne(userRequest.getAuthenticationId());
            if(!authentication.isEmailVerified() || !authentication.isPhoneVerified()){
                log.error("User not verified log");
                throw new HotifiException("User not verified", new ErrorCode("User not verified", null, 501));
            }
            User user = new User();
            authentication.setActivated(true);
            setUser(userRequest, user, authentication);
            authenticationRepository.save(authentication);
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Not Authenticated", e);
            throw new DataIntegrityViolationException("Not Authenticted", e);
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    //To check if username is available in database
    @Override
    @Transactional
    public User getUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            log.error("Error ", e);
        }
        return null;
    }

    @Override
    @Transactional
    public void updateUser(UserRequest userUpdateRequest) {
        try {
            Long authenticationId = userUpdateRequest.getAuthenticationId();
            User user = userRepository.findByAuthenticationId(authenticationId);
            if(user == null)
                throw new Exception("User doesn't exist");
            if(!isUserLegit(user))
                throw new Exception("User not legit to be updated");
            Authentication authentication = user.getAuthentication();
            setUser(userUpdateRequest, user, authentication);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    @Transactional
    @Override
    public void updateLoginStatus(Long id, boolean loginStatus) {
        try {
            User user = userRepository.getOne(id);
            if(!isUserLegit(user))
                throw new Exception("User not legit to be updated");
            if (user.isLoggedIn() && loginStatus) {
                log.error("Already logged in");
                throw new Exception("Already logged in");
            } else if (!user.isLoggedIn() && !loginStatus) {
                log.error("Already logged out");
                throw new Exception("Already logged out");
            } else {
                user.setLoggedIn(loginStatus);
                userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long id, boolean deleteUser) {
        try {
            User user = userRepository.getOne(id);
            Long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.getOne(authenticationId);

            if (authentication.isDeleted()) {
                log.error("Account already deleted");
                throw new Exception("Account already deleted");
            }

            authentication.setDeleted(true);
            authentication.setEmail(null);
            authentication.setPhone(null);
            authenticationRepository.save(authentication);

            user.setFacebookId(null);
            user.setGoogleId(null);
            user.setLoggedIn(false);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    //setting up user's values
    public void setUser(UserRequest userRequest, User user, Authentication authentication) {
        user.setAuthentication(authentication);
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setFacebookId(userRequest.getFacebookId());
        user.setGoogleId(userRequest.getGoogleId());
        user.setUsername(userRequest.getUsername());
        user.setPhotoUrl(userRequest.getPhotoUrl());
        user.setDateOfBirth(userRequest.getDateOfBirth());
    }

    //returns true if user is legit
    //Not checking for verified user since while adding user task has already been completed
    //because user would not be created if email and phone have not been verified
    public boolean isUserLegit(User user){
        if(user == null)
            return false;
        //login check not required
        return !user.getAuthentication().isDeleted() && user.getAuthentication().isActivated() && !user.getAuthentication().isBanned() && !user.getAuthentication().isFreezed();
    }
}