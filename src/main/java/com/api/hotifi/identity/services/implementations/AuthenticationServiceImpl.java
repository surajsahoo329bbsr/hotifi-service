package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.authorization.utils.AuthorizationUtils;
import com.api.hotifi.common.constant.Constants;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.processors.codes.SocialCodes;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.services.interfaces.ISocialService;
import com.api.hotifi.common.utils.AESUtils;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.Role;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.errors.UserErrorMessages;
import com.api.hotifi.identity.models.RoleName;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.RoleRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.utils.OtpUtils;
import com.api.hotifi.identity.web.response.CredentialsResponse;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Console;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final AuthenticationRepository authenticationRepository;
    private final RoleRepository roleRepository;
    private final IEmailService emailService;
    private final ISocialService socialService;

    public AuthenticationServiceImpl(AuthenticationRepository authenticationRepository, RoleRepository roleRepository, IEmailService emailService, ISocialService socialService) {
        this.authenticationRepository = authenticationRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.socialService = socialService;
    }

    @Transactional
    @Override
    public Authentication getAuthentication(String email, boolean isAdmin) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (isAdmin)
            authentication.setPassword(null);
        return authentication;
    }

    @Transactional
    @Override
    //If login client already has email verified no need for further verification
    public CredentialsResponse addEmail(String email, String identifier, String token, String socialClient) {
        if ((socialClient != null && token == null) || (socialClient == null && token != null))
            throw new HotifiException(UserErrorCodes.USER_SOCIAL_TOKEN_OR_IDENTIFIER_NOT_FOUND);
        if (!socialService.isSocialUserVerified(token, identifier, email, SocialCodes.valueOf(socialClient)))
            throw new HotifiException(UserErrorCodes.USER_SOCIAL_IDENTIFIER_INVALID);
        try {
            boolean isEmailVerified = socialClient != null; //Do any not null check for social client or token
            Authentication authentication = new Authentication();
            Role role = roleRepository.findByRoleName(RoleName.CUSTOMER.name());
            String uuid = UUID.randomUUID().toString();
            String encryptedPassword = AESUtils.encrypt(uuid, Constants.AES_PASSWORD_SECRET_KEY);
            authentication.setEmail(email);
            authentication.setEmailVerified(isEmailVerified);
            authentication.setPassword(encryptedPassword);
            authentication.setRoles(Collections.singletonList(role));
            if (!isEmailVerified) //If email not verified, send email otps to verify as usual process
                OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);
            else {
                Date modifiedAt = new Date(System.currentTimeMillis());
                authentication.setModifiedAt(modifiedAt);
                authenticationRepository.save(authentication);
                String decryptedPassword = AESUtils.decrypt(authentication.getPassword(), Constants.AES_PASSWORD_SECRET_KEY);
                return new CredentialsResponse(authentication.getEmail(), decryptedPassword);
            }
        } catch (DataIntegrityViolationException e) {
            log.error(UserErrorMessages.USER_EXISTS);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_EXISTS);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
            throw new HotifiException(AuthenticationErrorCodes.UNEXPECTED_AUTHENTICATION_ERROR);
        }
        return null;
    }

    @Transactional
    @Override
    public void resendEmailOtpSignUp(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        //Since it is signup so no need for verifying legit user
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_VERIFIED);
        //If token created at is null, it means otp is generated for first time or Otp duration expired and we are setting new Otp
        log.info("Regenerating Otp...");
        OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);

    }

    @Transactional
    @Override
    public void verifyEmail(String email, String emailOtp) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        String encryptedEmailOtp = authentication.getEmailOtp();
        if (authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_VERIFIED);
        if (OtpUtils.isEmailOtpExpired(authentication)) {
            log.error("Otp Expired");
            authentication.setEmailOtp(null);
            authenticationRepository.save(authentication);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_EXPIRED);
        }
        if (BCrypt.checkpw(emailOtp, encryptedEmailOtp)) {
            authentication.setEmailOtp(null);
            authentication.setEmailVerified(true);
            authenticationRepository.save(authentication);
            log.info("User Email Verified");
        } else {
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_OTP_INVALID);
        }
    }

    @Transactional
    @Override
    public void verifyPhone(String email, String countryCode, String phone) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        if (!authentication.isEmailVerified())
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_NOT_VERIFIED);
        if (authentication.isPhoneVerified())
            throw new HotifiException(AuthenticationErrorCodes.PHONE_ALREADY_VERIFIED);
        try {
            authentication.setCountryCode(countryCode);
            authentication.setPhone(phone);
            authentication.setPhoneVerified(true);
            authenticationRepository.save(authentication);
        } catch (DataIntegrityViolationException e) {
            throw new HotifiException(AuthenticationErrorCodes.PHONE_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new HotifiException(AuthenticationErrorCodes.UNEXPECTED_AUTHENTICATION_ERROR);
        }
    }

    @Override
    public boolean isPhoneAvailable(String phone) {
        return !authenticationRepository.existsByPhone(phone);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (email == null)
            throw new UsernameNotFoundException("Email not found");
        List<GrantedAuthority> authorities = authentication.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        String decryptedPassword = AESUtils.decrypt(authentication.getPassword(), Constants.AES_PASSWORD_SECRET_KEY);
        return new User(authentication.getEmail(), decryptedPassword, authorities);
    }
}
