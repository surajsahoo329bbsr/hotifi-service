package com.api.hotifi.configuration;

import com.api.hotifi.common.properties.OAuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfiguration {

    /*@Bean
    public IAuthenticationService authenticationService(AuthenticationRepository authenticationRepository) {
        return new AuthenticationServiceImpl(authenticationRepository);
    }*/

    @Bean
    public OAuthProperties oauthProperties() {
        return new OAuthProperties();
    }

}
