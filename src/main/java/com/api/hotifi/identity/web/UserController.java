package com.api.hotifi.identity.web;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.service.IAuthenticationService;
import com.api.hotifi.identity.service.IUserService;
import com.api.hotifi.identity.web.request.UserRequest;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@Api(tags = Constants.USER_TAG)
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthenticationService authenticationService;

    @PostMapping(path = "/add")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRequest userRequest){
        userService.addUser(userRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/get")
    public ResponseEntity<?> getUserByUsername(String username){
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<?> updateUser(long id, @RequestBody @Valid UserRequest userRequest){
        User user = userService.updateUser(id, userRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/update/login")
    public ResponseEntity<?> updateUserLogin(long id, boolean loginStatus){
        userService.updateLoginStatus(id, loginStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path="/delete")
    public ResponseEntity<?> deleteUser(long id, boolean deleteUser){
        userService.deleteUser(id, deleteUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
