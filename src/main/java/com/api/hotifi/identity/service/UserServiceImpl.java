package com.api.hotifi.identity.service;

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
            Authentication authentication = authenticationRepository.findById(userRequest.getAuthenticationId());
            User user = new User();
            setUser(userRequest, user, authentication);
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Not Authenticated", e);
            throw new DataIntegrityViolationException("Not Authenticted", e);
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

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
    public User updateUser(long id, UserRequest userUpdateRequest) {
        try {
            User user = userRepository.getOne(id);
            Authentication authentication = user.getAuthentication();
            User updatedUser = setUser(userUpdateRequest, user, authentication);
            userRepository.save(updatedUser);
            return updatedUser;
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }

    @Transactional
    @Override
    public void updateLoginStatus(long id, boolean loginStatus) {
        try {
            User user = userRepository.getOne(id);
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
    public void deleteUser(long id, boolean deleteUser) {
        try {
            User user = userRepository.getOne(id);
            long authenticationId = user.getAuthentication().getId();
            Authentication authentication = authenticationRepository.findById(authenticationId);
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
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error ", e);
        }
    }

    //setting up user's values
    public User setUser(UserRequest userRequest, User user, Authentication authentication) {
        user.setAuthentication(authentication);
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setFacebookId(userRequest.getFacebookId());
        user.setGoogleId(userRequest.getGoogleId());
        user.setUsername(user.getUsername());
        user.setPhotoUrl(user.getPhotoUrl());
        user.setDateOfBirth(user.getDateOfBirth());
        return user;
    }
}
