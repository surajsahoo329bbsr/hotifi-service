package com.api.hotifi.identity.web.controller;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.web.request.EmailOtpRequest;
import com.api.hotifi.identity.web.request.PhoneRequest;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    //On App start first this method will be called.
    @GetMapping(path = "/email/get/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEmail(@PathVariable(value = "email")
                                          @NotBlank(message = "{email.blank}")
                                          @Email(message = "{email.pattern.invalid}")
                                          @Length(max = 255, message = "{email.length.invalid}") String email) {
        Authentication authentication = authenticationService.getAuthentication(email);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @PostMapping(path = "/email/add/{email}/{is-verified}")
    public ResponseEntity<?> addEmail(@PathVariable(value = "email")
                                          @NotBlank(message = "{email.blank}")
                                          @Email(message = "{email.pattern.invalid}")
                                          @Length(max = 255, message = "{email.length.invalid}") String email, @PathVariable(value = "is-verified")boolean isEmailVerified){
        String token  = authenticationService.addEmail(email, isEmailVerified);
        return new ResponseEntity<>(token, HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/email/sign-up/otp/{email}")
    public ResponseEntity<?> generateEmailOtpSignUp(@PathVariable(value = "email")
                                                        @NotBlank(message = "{email.blank}")
                                                        @Email(message = "{email.pattern.invalid}")
                                                        @Length(max = 255, message = "{email.length.invalid}") String email) {
        String token = authenticationService.generateEmailOtpSignUp(email, false);
        return new ResponseEntity<>(token, HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/email/sign-up/resend/otp/{email}")
    public ResponseEntity<?> regenerateEmailOtpSignUp(@PathVariable(value = "email")
                                                    @NotBlank(message = "{email.blank}")
                                                    @Email(message = "{email.pattern.invalid}")
                                                    @Length(max = 255, message = "{email.length.invalid}") String email) {
        String token = authenticationService.generateEmailOtpSignUp(email, true);
        return new ResponseEntity<>(token, HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/email/verify/otp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyEmailOtp(@RequestBody @Valid EmailOtpRequest emailOtpRequest) {
        authenticationService.verifyEmailOtp(emailOtpRequest.getEmail(), emailOtpRequest.getOtp());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/phone/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPhoneUser(@RequestBody @Valid PhoneRequest phoneRequest) {
        authenticationService.verifyPhone(phoneRequest.getEmail(), phoneRequest.getCountryCode(), phoneRequest.getPhone());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}