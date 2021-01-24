package com.api.hotifi.identity.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.web.request.UserRequest;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
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
    private UserRepository userRepository;

    @Autowired
    private IAuthenticationService authenticationService;

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRequest userRequest) {
        userService.addUser(userRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/get/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username")
                                               @NotBlank(message = "{username.blank}")
                                               @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/get/is-available/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isUsernameAvailable(@PathVariable(value = "username")
                                                 @NotBlank(message = "{username.blank}")
                                                 @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        boolean isUsernameAvailable = userService.isUsernameAvailable(username);
        return new ResponseEntity<>(isUsernameAvailable, HttpStatus.OK);
    }

    @GetMapping(path = "/get/social/{identifier-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByIdentifier(@PathVariable(value = "identifier-id")
                                                 @NotBlank(message = "{identifier.id.blank}")
                                                 @Length(max = 255, message = "{identifier.id.length.invalid}") String identifier) {
        User user = userRepository.findByFacebookId(identifier) == null ? userRepository.findByGoogleId(identifier) : null;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/login/send-otp/{email}")
    public ResponseEntity<?> generateEmailOtpLogin(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        String token = userService.generateEmailOtpLogin(email);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PutMapping(path = "/login/resend-otp/{email}")
    public ResponseEntity<?> regenerateEmailOtpLogin(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        String token = userService.regenerateEmailOtpLogin(email);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PutMapping(path = "/login/{email}/{email-otp}")
    public ResponseEntity<?> verifyEmailOtp(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email, @PathVariable(value = "email-otp") String emailOtp) {
        userService.verifyEmailOtpAndLogin(email, emailOtp);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserRequest userRequest) {
        userService.updateUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/update/login/{id}/{login-status}")
    public ResponseEntity<?> updateUserLogin(@PathVariable(value = "id") @Range(min = 1, message = "{user.id.invalid}") Long id, @PathVariable(value = "login-status") boolean loginStatus) {
        userService.updateLoginStatus(id, loginStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
