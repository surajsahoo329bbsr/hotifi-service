package com.api.hotifi.identity.web;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.service.IAuthenticationService;
import com.api.hotifi.identity.web.request.EmailOtpRequest;
import com.api.hotifi.identity.web.request.EmailRequest;
import com.api.hotifi.identity.web.request.PhoneRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "/auth")
public class AuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;

    //On App start first this method will be called.
    @GetMapping(path = "/get")
    public ResponseEntity<?> getEmail(@RequestBody @Valid EmailRequest emailRequest){
        Authentication authentication = authenticationService.getAuthentication(emailRequest.getEmail());
        if(authentication != null)
            return new ResponseEntity<>(authentication, HttpStatus.FOUND);
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "/email-otp-login")
    public ResponseEntity<?> generateEmailOtpLogin(@RequestBody @Valid EmailRequest emailRequest){
        //return User Object If Found
        String emailOtp = authenticationService.generateEmailOtpLogin(emailRequest.getEmail());
        if(emailOtp.matches(Constants.VALID_OTP_PATTERN))
            return new ResponseEntity<>(emailOtp, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addEmail(@RequestBody @Valid EmailRequest emailRequest, boolean isEmailVerified) {
        authenticationService.addEmail(emailRequest.getEmail(), isEmailVerified);
        Authentication authentication = authenticationService.getAuthentication(emailRequest.getEmail());
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @PutMapping(path = "/email-otp-verify")
    public ResponseEntity<?> verifyEmailOtp(@RequestBody @Valid EmailOtpRequest emailOtpRequest){
        boolean isEmailVerified = authenticationService.verifyEmailOtp(emailOtpRequest.getEmail(), emailOtpRequest.getOtp());
        if(isEmailVerified)
            //return UserRequest instead of emailOtpRequest
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping(path = "/phone-verify")
    public ResponseEntity<?> verifyPhoneUser(@RequestBody @Valid PhoneRequest phoneRequest){
        boolean isPhoneVerified = authenticationService.verifyPhoneUser(phoneRequest.getEmail(),phoneRequest.getCountryCode(), phoneRequest.getPhone());
        if(isPhoneVerified)
            return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE);
    }
}
