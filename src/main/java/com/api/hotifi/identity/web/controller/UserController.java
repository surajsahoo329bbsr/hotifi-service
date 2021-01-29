package com.api.hotifi.identity.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.web.request.UserRequest;
import com.api.hotifi.identity.web.response.UsernameAvailabilityResponse;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add User Details",
            notes = "Add User Details",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRequest userRequest) {
        userService.addUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get User Details By Username",
            notes = "Get User Details By Username",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username")
                                               @NotBlank(message = "{username.blank}")
                                               @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        User user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/is-available/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Checks If Username Is Available Or Not",
            notes = "Checks If Username Is Available Or Not",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> isUsernameAvailable(@PathVariable(value = "username")
                                                 @NotBlank(message = "{username.blank}")
                                                 @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        boolean isUsernameAvailable = userService.isUsernameAvailable(username);
        return new ResponseEntity<>(new UsernameAvailabilityResponse(isUsernameAvailable), HttpStatus.OK);
    }

    @GetMapping(path = "/social/{identifier-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get User By Social Network Id",
            notes = "Get User By Social Network Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserByIdentifier(@PathVariable(value = "identifier-id")
                                                 @NotBlank(message = "{identifier.id.blank}")
                                                 @Length(max = 255, message = "{identifier.id.length.invalid}") String identifier) {
        User user = userRepository.findByFacebookId(identifier) == null ? userRepository.findByGoogleId(identifier) : null;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/login/send/otp/{email}")
    @ApiOperation(
            value = "Send Email Otp By Providing Email For Login",
            notes = "Send Email Otp By Providing Email For Login",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> sendEmailOtpLogin(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        userService.sendEmailOtpLogin(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/login/resend/otp/{email}")
    @ApiOperation(
            value = "Resend Email Otp By Providing Email For Login",
            notes = "Resend Email Otp By Providing Email For Login",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> resendEmailOtpLogin(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        userService.resendEmailOtpLogin(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/login/verify/{email}/{email-otp}")
    @ApiOperation(
            value = "Verify Email Otp By Providing Email For Login",
            notes = "Verify Email Otp By Providing Email For Login",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR')")
    public ResponseEntity<?> verifyEmailOtp(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email, @PathVariable(value = "email-otp") String emailOtp) {
        userService.verifyEmailOtpAndLogin(email, emailOtp);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Verify Email Otp By Providing Email For Login",
            notes = "Verify Email Otp By Providing Email For Login",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserRequest userRequest) {
        userService.updateUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/login/update/{id}/{login-status}")
    @ApiOperation(
            value = "Verify Email Otp By Providing Email For Login",
            notes = "Verify Email Otp By Providing Email For Login",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINSTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUserLogin(@PathVariable(value = "id") @Range(min = 1, message = "{user.id.invalid}") Long id, @PathVariable(value = "login-status") boolean loginStatus) {
        userService.updateLoginStatus(id, loginStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
