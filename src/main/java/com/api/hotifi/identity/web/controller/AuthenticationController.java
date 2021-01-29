package com.api.hotifi.identity.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.errors.ErrorMessages;
import com.api.hotifi.common.exception.errors.ErrorResponse;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.web.request.EmailOtpRequest;
import com.api.hotifi.identity.web.request.PhoneRequest;
import com.api.hotifi.identity.web.response.PasswordTokenResponse;
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

@Validated
@RestController
@Api(tags = Constants.AUTHENTICAION_TAG)
@RequestMapping(path = "/authenticate")
public class AuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;

    @GetMapping(path = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Get Authentication Details By Email",
            notes = "Get Authentication Details By Email",
            response = String.class)
    @ApiResponses(value = @ApiResponse(code = 500, message = ErrorMessages.INTERNAL_ERROR, response = ErrorResponse.class))
    @ApiImplicitParams(value = @ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> getEmail(@PathVariable(value = "email")
                                      @NotBlank(message = "{email.blank}")
                                      @Email(message = "{email.pattern.invalid}")
                                      @Length(max = 255, message = "{email.length.invalid}") String email) {
        Authentication authentication = authenticationService.getAuthentication(email);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @PostMapping(path = "/sign-up/{email}/{is-verified}")
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
                                      @Length(max = 255, message = "{email.length.invalid}") String email, @PathVariable(value = "is-verified") boolean isEmailVerified) {
        String passwordToken = authenticationService.addEmail(email, isEmailVerified);
        return new ResponseEntity<>(new PasswordTokenResponse(passwordToken), HttpStatus.OK);
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