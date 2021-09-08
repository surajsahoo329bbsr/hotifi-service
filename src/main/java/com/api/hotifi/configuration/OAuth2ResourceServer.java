package com.api.hotifi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@EnableResourceServer
@Configuration
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "hotifi-rest-api";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID)
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers("/authenticate/**", "/user/",
                        "/user/is-available/**", "/user/login/otp/**", "/email/**", "/user/facebook/**",
                        "/user/password/reset/**",
                        "/user/login/verify/**").permitAll();
    }
}


