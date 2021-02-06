package com.api.hotifi.identity.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.identity.entities.Authentication;
import com.api.hotifi.identity.entities.Role;
import com.api.hotifi.identity.errors.AuthenticationErrorCodes;
import com.api.hotifi.identity.errors.UserErrorMessages;
import com.api.hotifi.identity.models.RoleName;
import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.RoleRepository;
import com.api.hotifi.identity.services.interfaces.IAuthenticationService;
import com.api.hotifi.identity.services.interfaces.IEmailService;
import com.api.hotifi.identity.utils.OtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

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

    public AuthenticationServiceImpl(AuthenticationRepository authenticationRepository, RoleRepository roleRepository, IEmailService emailService) {
        this.authenticationRepository = authenticationRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public Authentication getAuthentication(String email) {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (authentication == null)
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_DOES_NOT_EXIST);
        return authentication;
    }

    @Transactional
    @Override
    //If login client already has email verified no need for further verification
    public String addEmail(String email, boolean isEmailVerified) {
        try {
            Authentication authentication = new Authentication();
            Role role = roleRepository.findByRoleName(RoleName.CUSTOMER.name());
            String token = UUID.randomUUID().toString();
            authentication.setEmail(email);
            authentication.setEmailVerified(isEmailVerified);
            authentication.setPassword(token);
            authentication.setRoles(Collections.singletonList(role));
            if (!isEmailVerified) {
                OtpUtils.saveAuthenticationEmailOtp(authentication, authenticationRepository, emailService);
            }else {
                Date modifiedAt = new Date(System.currentTimeMillis());
                authentication.setModifiedAt(modifiedAt);
                authenticationRepository.save(authentication);
            }
            return token;
        } catch (DataIntegrityViolationException e) {
            log.error(UserErrorMessages.USER_EXISTS);
            throw new HotifiException(AuthenticationErrorCodes.EMAIL_ALREADY_EXISTS);
        } catch (Exception e) {
            log.error("Error Occurred ", e);
            throw new HotifiException(AuthenticationErrorCodes.UNEXPECTED_AUTHENTICATION_ERROR);
        }

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

    //@Transaction cannot be added here
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Authentication authentication = authenticationRepository.findByEmail(email);
        if (email == null) throw new UsernameNotFoundException("Email not found");
        List<GrantedAuthority> authorities = authentication.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
        return new User(authentication.getEmail(), authentication.getPassword(), authorities);
    }
}
