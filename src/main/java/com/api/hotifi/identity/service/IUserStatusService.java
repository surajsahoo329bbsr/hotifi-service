package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.UserStatus;
import com.api.hotifi.identity.web.request.UserStatusRequest;

import java.util.List;

public interface IUserStatusService {

    List<UserStatus> addUserStatus(UserStatusRequest userStatusRequest);

    List<UserStatus> getUserStatusByUserId(Long userId);

    void deleteUser(Long id);

    void activateUser(Long id, boolean activateUser);

    void banUser(Long id, boolean banUser);

    void freezeUser(Long id, boolean freezeUser);

}
