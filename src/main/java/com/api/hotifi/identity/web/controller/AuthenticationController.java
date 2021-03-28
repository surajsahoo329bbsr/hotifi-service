package com.api.hotifi.identity.web.controller;

import com.api.hotifi.authorization.service.ICustomerAuthorizationService;
import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.common.validator.SocialClient;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.web.request.EmailOtpRequest;
import com.api.hotifi.identity.web.request.PhoneRequest;
import com.api.hotifi.identity.web.response.AvailabilityResponse;
import com.api.hotifi.identity.web.response.CredentialsResponse;
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
@Api(tags = Constants.AUTHENTICATION_TAG)
@RequestMapping(path = "/authenticate")
public class AuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private ICustomerAuthorizationService customerAuthorizationService;

    @GetMapping(path = "/administrator/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Authentication Details By Email For Administrator",
            notes = "Get Authentication Details By Email For Administrator",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getAuthenticationForAdministrator(@PathVariable(value = "email")
                                                               @NotBlank(message = "{email.blank}")
                                                               @Email(message = "{email.pattern.invalid}")
                                                               @Length(max = 255, message = "{email.length.invalid}") String email) {
        Authentication authentication = authenticationService.getAuthentication(email, true);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @GetMapping(path = "/customer/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Authentication Details By Email For Customer",
            notes = "Get Authentication Details By Email For Customer",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getAuthenticationForCustomer(@PathVariable(value = "email")
                                                          @NotBlank(message = "{email.blank}")
                                                          @Email(message = "{email.pattern.invalid}")
                                                          @Length(max = 255, message = "{email.length.invalid}") String email) {
        Authentication authentication = (AuthorizationUtils.isAdministratorRole() || customerAuthorizationService.isAuthorizedByEmail(email, AuthorizationUtils.getUserToken())) ? authenticationService.getAuthentication(email, false) : null;
        return new ResponseEntity<>(authentication, HttpStatus.OK);
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
        boolean isPhoneAvailable = authenticationService.isPhoneAvailable(phone);
        return new ResponseEntity<>(new AvailabilityResponse(null, isPhoneAvailable, null), HttpStatus.OK);
    }

    @GetMapping(path = "/is-available/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Checks If Email Is Available Or Not",
            notes = "Checks If Email Is Available Or Not",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<?> isEmailAvailable(@PathVariable(value = "email")
                                              @NotBlank(message = "{email.blank}")
                                              @Email(message = "{email.invalid}") String email) {
        //No need to check for role security here
        boolean isEmailAvailable = authenticationService.isEmailAvailable(email);
        return new ResponseEntity<>(new AvailabilityResponse(null, null, isEmailAvailable), HttpStatus.OK);
    }

    @PostMapping(path = "/sign-up/{email}")
    @ApiOperation(
            value = "Post Authentication By Email",
            notes = "Post Authentication By Verified Or Unverified Email And Sends Password Token",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> addEmail(@PathVariable(value = "email")
                                      @NotBlank(message = "{email.blank}")
                                      @Email(message = "{email.pattern.invalid}")
                                      @Length(max = 255, message = "{email.length.invalid}") String email,
                                      @ApiParam(name = "identifier", type = "String")
                                      @Length(max = 255, message = "{id.identifier.length.invalid}") @RequestParam String identifier,
                                      @ApiParam(name = "token", type = "String")
                                      @Length(max = 255, message = "{id.token.length.invalid}") @RequestParam String token,
                                      @ApiParam(name = "social-client", type = "String")
                                      @SocialClient @RequestParam(name = "social-client") String socialClient) {
        CredentialsResponse credentialsResponse = authenticationService.addEmail(email, identifier, token, socialClient);
        return new ResponseEntity<>(credentialsResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/sign-up/resend/otp/{email}")
    @ApiOperation(
            value = "Resend Email Otp For Sign-Up",
            notes = "Resend Email Otp For Sign-Up",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> resendEmailOtpSignUp(@PathVariable(value = "email")
                                                  @NotBlank(message = "{email.blank}")
                                                  @Email(message = "{email.pattern.invalid}")
                                                  @Length(max = 255, message = "{email.length.invalid}") String email) {
        authenticationService.resendEmailOtpSignUp(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/sign-up/verify/email", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Verify Email For Sign-Up",
            notes = "Verify Email For Sign-Up",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid EmailOtpRequest emailOtpRequest) {
        authenticationService.verifyEmail(emailOtpRequest.getEmail(), emailOtpRequest.getOtp());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/sign-up/verify/phone", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Verify Phone For Sign-Up",
            notes = "Verify Phone For Sign-Up",
            code = 204,
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer Token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> verifyPhone(@RequestBody @Valid PhoneRequest phoneRequest) {
        authenticationService.verifyPhone(phoneRequest.getEmail(), phoneRequest.getCountryCode(), phoneRequest.getPhone());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}