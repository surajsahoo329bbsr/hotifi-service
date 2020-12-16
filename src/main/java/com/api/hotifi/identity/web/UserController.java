package com.api.hotifi.identity.web;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entity.User;
import com.api.hotifi.identity.service.IAuthenticationService;
import com.api.hotifi.identity.service.IUserService;
import com.api.hotifi.identity.web.request.UserRequest;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Validated
@RestController
@Api(tags = Constants.USER_TAG)
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthenticationService authenticationService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRequest userRequest) {
        userService.addUser(userRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/get/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username")
                                               @NotBlank @Pattern(regexp = Constants.VALID_USERNAME_PATTERN) String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/login/{id}")
    public ResponseEntity<?> generateEmailOtpLogin(@PathVariable(value = "id") Long id) {
        userService.generateEmailOtpLogin(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/login/{id}/{email-otp}")
    public ResponseEntity<?> verifyEmailOtp(@PathVariable(value = "id") Long id, @PathVariable(value = "email-otp") String emailOtp) {
        userService.verifyEmailOtp(id, emailOtp);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserRequest userRequest) {
        userService.updateUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/update/login/{id}/{login-status}")
    public ResponseEntity<?> updateUserLogin(@PathVariable(value = "id") Long id, @PathVariable(value = "login-status") boolean loginStatus) {
        userService.updateLoginStatus(id, loginStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
