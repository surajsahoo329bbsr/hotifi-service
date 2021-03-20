package com.api.hotifi.identity.web.controller;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.web.request.UserRequest;
import com.api.hotifi.identity.web.response.AvailabilityResponse;
import com.api.hotifi.identity.web.response.CredentialsResponse;
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

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Add User Details",
            notes = "Add User Details",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
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
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username")
                                               @NotBlank(message = "{username.blank}")
                                               @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        User user = (AuthorizationUtils.isAdministratorRole() ||
                customerAuthorizationService.isAuthorizedByUsername(username, AuthorizationUtils.getUserToken())) ?
                userService.getUserByUsername(username) : null;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/is-available/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Checks If Username Is Available Or Not",
            notes = "Checks If Username Is Available Or Not",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> isUsernameAvailable(@PathVariable(value = "username")
                                                 @NotBlank(message = "{username.blank}")
                                                 @Pattern(regexp = Constants.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        //No need to check for role security here
        boolean isUsernameAvailable = userService.isUsernameAvailable(username);
        return new ResponseEntity<>(new AvailabilityResponse(isUsernameAvailable, null), HttpStatus.OK);
    }

    @GetMapping(path = "/is-available/phone/{phone}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Checks If Phone Number Is Available Or Not",
            notes = "Checks If Phone Number Is Available Or Not",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> isPhoneAvailable(@PathVariable(value = "phone")
                                              @NotBlank(message = "{phone.blank}")
                                              @Pattern(regexp = Constants.VALID_PHONE_PATTERN, message = "{phone.invalid}") String phone) {
        //No need to check for role security here
        boolean isPhoneAvailable = userService.isPhoneAvailable(phone);
        return new ResponseEntity<>(new AvailabilityResponse(null, isPhoneAvailable), HttpStatus.OK);
    }

    @GetMapping(path = "/social/{identifier-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get User By Social Network Id",
            notes = "Get User By Social Network Id",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserByIdentifier(@PathVariable(value = "identifier-id")
                                                 @NotBlank(message = "{identifier.id.blank}")
                                                 @Length(max = 255, message = "{identifier.id.length.invalid}") String identifier) {
        User socialUser = userRepository.findByFacebookId(identifier) == null ? userRepository.findByGoogleId(identifier) : null;
        User user = (AuthorizationUtils.isAdministratorRole() ||
                customerAuthorizationService.isAuthorizedBySocialId(identifier, AuthorizationUtils.getUserToken())) ?
                socialUser : null;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/login/send/otp/{email}")
    @ApiOperation(
            value = "Send Email Otp By Providing Email For Login",
            notes = "Send Email Otp By Providing Email For Login",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
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
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
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
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> verifyEmailOtp(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email, @PathVariable(value = "email-otp") String emailOtp) {
        CredentialsResponse credentialsResponse = userService.verifyEmailOtp(email, emailOtp);
        return new ResponseEntity<>(credentialsResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/login/{email}")
    @ApiOperation(
            value = "Update user login by email",
            notes = "Update user login by email",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUserLogin(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        if (customerAuthorizationService.isAuthorizedByEmail(email, AuthorizationUtils.getUserToken()))
            userService.updateUserLogin(email, true);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/logout/{email}")
    @ApiOperation(
            value = "Update user logout by email",
            notes = "Update user logout by email",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUserLogout(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        if (customerAuthorizationService.isAuthorizedByEmail(email, AuthorizationUtils.getUserToken()))
            userService.updateUserLogin(email, false);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Update User Details",
            notes = "Update User Details",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserRequest userRequest) {
        if ((AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedByAuthenticationId(userRequest.getAuthenticationId(), AuthorizationUtils.getUserToken())))
            userService.updateUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/login/update/{id}/{login-status}")
    @ApiOperation(
            value = "Update User Login Status",
            notes = "Update User Login Status",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUserLogin(@PathVariable(value = "id") @Range(min = 1, message = "{user.id.invalid}") Long id, @PathVariable(value = "login-status") boolean loginStatus) {
        if ((AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedByUserId(id, AuthorizationUtils.getUserToken())))
            userService.updateLoginStatus(id, loginStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
