package com.api.hotifi.authorization.utils;

import com.api.hotifi.identity.models.RoleName;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class AuthorizationUtils {

    public static String getUserToken(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return  ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
    }

    public static boolean isAdminstratorRole(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication
                .getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(RoleName.ADMINISTRATOR.name()));
    }

}
