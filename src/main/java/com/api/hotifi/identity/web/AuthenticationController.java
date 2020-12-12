package com.api.hotifi.identity.web;

import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.identity.entity.Authentication;
import com.api.hotifi.identity.service.IAuthenticationService;
import com.api.hotifi.identity.web.request.EmailOtpRequest;
import com.api.hotifi.identity.web.request.PhoneRequest;
import io.swagger.annotations.Api;
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
@Api(tags = Constants.AUTH_TAG)
@RequestMapping(path = "/auth")
public class AuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;

    //On App start first this method will be called.
    @GetMapping(path = "/email/get/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEmail(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                                        @Email(message = "{invalid.email}") String email) {
        Authentication authentication = authenticationService.getAuthentication(email);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @PutMapping(path = "/email/generate/otp/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateEmailOtpLogin(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                                       @Email(message = "{invalid.email}") String email) {
        authenticationService.generateEmailOtpLogin(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/email/add/{email}/{is-verified}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addEmail(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                          @Email(message = "{invalid.email}") String email, @PathVariable(value = "is-verified")boolean isEmailVerified){
        authenticationService.addEmail(email, isEmailVerified);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/email/verify/otp", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyEmailOtp(@RequestBody @Valid EmailOtpRequest emailOtpRequest) {
        authenticationService.verifyEmailOtp(emailOtpRequest.getEmail(), emailOtpRequest.getOtp());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/phone/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPhoneUser(@RequestBody @Valid PhoneRequest phoneRequest) {
        authenticationService.verifyPhoneUser(phoneRequest.getEmail(), phoneRequest.getCountryCode(), phoneRequest.getPhone());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/user/ban/{email}/{ban-user}")
    public ResponseEntity<?> banUser(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                         @Email(message = "{invalid.email}") String email, @PathVariable(value = "ban-user") boolean banUser) {
        authenticationService.banUser(email, banUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/user/activate/{email}/{activate-user}")
    public ResponseEntity<?> activateUser(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                              @Email(message = "{invalid.email}") String email, @PathVariable(value = "activate-user") boolean activateUser) {
        authenticationService.activateUser(email, activateUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/user/freeze/{email}/{freeze-user}")
    public ResponseEntity<?> freezeUser(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                            @Email(message = "{invalid.email}") String email, @PathVariable(value = "freeze-user") boolean freezeUser) {
        authenticationService.freezeUser(email, freezeUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/user/delete/{email}/{delete-user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable(value = "email") @NotBlank(message = "{email.empty}")
                                            @Email(message = "{invalid.email}") String email, @PathVariable(value = "delete-user") boolean deleteUser) {
        authenticationService.deleteUser(email, deleteUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}