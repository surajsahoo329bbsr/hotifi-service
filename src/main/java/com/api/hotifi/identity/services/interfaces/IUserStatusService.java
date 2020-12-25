package com.api.hotifi.identity.services.interfaces;

import com.api.hotifi.identity.entities.UserStatus;
import com.api.hotifi.identity.web.request.UserStatusRequest;

import java.util.List;

public interface IUserStatusService {

    List<UserStatus> addUserStatus(UserStatusRequest userStatusRequest);

    List<UserStatus> getUserStatusByUserId(Long userId);

    void freezeUser(Long id, boolean freezeUser);

}
