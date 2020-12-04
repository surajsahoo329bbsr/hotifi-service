package com.api.hotifi.identity.web;

import com.api.hotifi.identity.service.IAuthenticationService;
import com.api.hotifi.identity.web.request.EmailOtpRequest;
import com.api.hotifi.identity.web.request.EmailRequest;
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

    @GetMapping(path = "/get")
    public ResponseEntity<?> getEmail(@RequestBody @Valid EmailRequest emailRequest){
        boolean isEmailAvailable = authenticationService.isEmailAvailable(emailRequest.getEmail());
        if(!isEmailAvailable)
            return new ResponseEntity<>(emailRequest, HttpStatus.FOUND);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addEmail(@RequestBody @Valid EmailRequest emailRequest, boolean isEmailVerified) {
        authenticationService.addEmail(emailRequest.getEmail(), isEmailVerified);
        return new ResponseEntity<>(emailRequest, HttpStatus.OK);
    }

    @PostMapping(path = "/otp-verify")
    public ResponseEntity<?> verifyEmaiLOtp(@RequestBody @Valid EmailOtpRequest emailOtpRequest){
        boolean isEmailVerified = authenticationService.verifyEmailOtp(emailOtpRequest.getEmail(), emailOtpRequest.getOtp());
        if(isEmailVerified)
            return new ResponseEntity<>(emailOtpRequest, HttpStatus.ACCEPTED);
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}
