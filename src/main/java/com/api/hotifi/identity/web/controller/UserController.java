package com.api.hotifi.identity.web.controller;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.constants.configurations.BusinessConfigurations;
import com.api.hotifi.common.constants.messages.SuccessMessages;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.common.validator.SocialClient;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IUserService;
import com.api.hotifi.identity.web.request.UserRequest;
import com.api.hotifi.identity.web.response.AvailabilityResponse;
import com.api.hotifi.identity.web.response.CredentialsResponse;
import com.api.hotifi.identity.web.response.FacebookDeletionResponse;
import com.api.hotifi.identity.web.response.FacebookDeletionStatusResponse;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Length;
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
@Api(tags = AppConfigurations.USER_TAG)
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
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRequest userRequest) {
        userService.addUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/facebook/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Delete Facebook User Data By Facebook Team",
            notes = "Delete Facebook User Data By Facebook Team",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = FacebookDeletionResponse.class)
    })
    public ResponseEntity<?> deleteFacebookUserData(@RequestParam("signed_request") String signedRequest) {
        FacebookDeletionResponse deletionResponse = userService.deleteFacebookUserData(signedRequest);
        return new ResponseEntity<>(deletionResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/facebook/deletion-status/{facebook-id}/{confirmation-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Facebook Deletion User Data Status By User",
            notes = "Get Facebook Deletion User Data Status By User",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = FacebookDeletionResponse.class)
    })
    public ResponseEntity<?> facebookDeletionStatusRequest(@PathVariable(value = "facebook-id") String facebookId,
                                                           @PathVariable(value = "confirmation-code") String confirmationCode) {
        FacebookDeletionStatusResponse deletionStatusResponse = userService.getFacebookDeletionStatus(facebookId, confirmationCode);
        return new ResponseEntity<>(deletionStatusResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/password/reset/custom/{email}/{email-otp}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Reset Password By Email Otp",
            notes = "Reset Password By Email Otp",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = CredentialsResponse.class)
    })
    public ResponseEntity<?> resetPasswordForCustomUser(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email,
                                                        @PathVariable(value = "email-otp") @NotBlank(message = "{email.otp.blank}") String emailOtp) {
        CredentialsResponse credentialsResponse = userService.resetPassword(email, emailOtp, null, null, null);
        return new ResponseEntity<>(credentialsResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/password/reset/social/{email}/{identifier}/{token}/{social-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Reset Password By Social Login Client",
            notes = "Reset Password By Social Login Client",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = CredentialsResponse.class)
    })
    public ResponseEntity<?> resetPasswordForSocialUser(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email,
                                                        @PathVariable(value = "identifier") @Length(max = 255, message = "{identifier.length.invalid}") @NotBlank(message = "{identifier.blank}") String identifier,
                                                        @PathVariable(value = "token") @Length(max = 4048, message = "{token.length.invalid}") @NotBlank(message = "{token.blank}") String token,
                                                        @PathVariable(value = "social-code") @Length(max = 255, message = "{social.code.length.invalid}") @SocialClient(message = "{social.code.blank}") String socialClient) {
        CredentialsResponse credentialsResponse = userService.resetPassword(email, null, identifier, token, SocialCodes.valueOf(socialClient));
        return new ResponseEntity<>(credentialsResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get User Details By Username",
            notes = "Get User Details By Username",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = User.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserByUsername(@PathVariable(value = "username")
                                               @NotBlank(message = "{username.blank}")
                                               @Pattern(regexp = BusinessConfigurations.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
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
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = AvailabilityResponse.class)
    })
    public ResponseEntity<?> isUsernameAvailable(@PathVariable(value = "username")
                                                 @NotBlank(message = "{username.blank}")
                                                 @Pattern(regexp = BusinessConfigurations.VALID_USERNAME_PATTERN, message = "{username.invalid}") String username) {
        //No need to check for role security here
        boolean isUsernameAvailable = userService.isUsernameAvailable(username);
        return new ResponseEntity<>(new AvailabilityResponse(isUsernameAvailable, null, null), HttpStatus.OK);
    }

    @GetMapping(path = "/social/{identifier-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get User By Social Network Id",
            notes = "Get User By Social Network Id",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = User.class)
    })
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

    @PutMapping(path = "/login/otp/send/{email}")
    @ApiOperation(
            value = "Send Email Otp By Providing Email For Login",
            notes = "Send Email Otp By Providing Email For Login",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    public ResponseEntity<?> sendEmailOtpLogin(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email) {
        userService.sendEmailOtpLogin(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/login/otp/resend/{email}")
    @ApiOperation(
            value = "Resend Email Otp By Providing Email For Login",
            notes = "Resend Email Otp By Providing Email For Login",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
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
    public ResponseEntity<?> verifyEmailOtp(@PathVariable(value = "email") @Email(message = "{user.email.invalid}") String email, @PathVariable(value = "email-otp") @NotBlank(message = "email.otp.blank") String emailOtp) {
        userService.verifyEmailOtpLogin(email, emailOtp);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/login/{email}")
    @ApiOperation(
            value = "Update User Login By Email",
            notes = "Update User Login By Email",
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
            value = "Update User Logout By Email",
            notes = "Update User Logout By Email",
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
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserRequest userRequest) {
        if (customerAuthorizationService.isAuthorizedByAuthenticationId(userRequest.getAuthenticationId(), AuthorizationUtils.getUserToken()))
            userService.updateUser(userRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/email/{email}")
    @ApiOperation(
            value = "Get User Details By Email",
            notes = "Get User Details By Email",
            code = 204,
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class),
            @ApiResponse(code = 200, message = SuccessMessages.OK, response = User.class)
    })
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getUserByEmail(@PathVariable(value = "email") @Email(message = "{email.invalid}") String email) {
        User user = ((AuthorizationUtils.isAdministratorRole()
                || customerAuthorizationService.isAuthorizedByEmail(email, AuthorizationUtils.getUserToken()))) ? userService.getUserByEmail(email) : null;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
