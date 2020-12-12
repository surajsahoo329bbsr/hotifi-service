package com.api.hotifi.identity.service;

import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.web.request.UserRequest;

public interface IUserService {

    void addUser(UserRequest userRequest);

    User getUserByUsername(String username);

    User updateUser(long id, UserRequest userRequest);

    void updateLoginStatus(long id, boolean loginStatus);

    void deleteUser(long id, boolean deleteUser);

}
