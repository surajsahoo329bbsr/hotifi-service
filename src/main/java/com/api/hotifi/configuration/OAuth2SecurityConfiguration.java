package com.api.hotifi.configuration;

import com.api.hotifi.identity.services.implementations.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class OAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationServiceImpl userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
        .antMatchers("/v2/api-docs", "/configuration/ui",
         "/swagger-resources/**", "/configuration/**",
         "/swagger-ui.html", "/webjars/**")
        .antMatchers(HttpMethod.GET, "/authenticate")
                .antMatchers(HttpMethod.GET, "/categories/**")
                .antMatchers(HttpMethod.GET, "/products/category/**")
                .antMatchers(HttpMethod.GET, "/products/category/**")
                .antMatchers(HttpMethod.GET, "/products/latest")
                .antMatchers(HttpMethod.GET, "/products/**")
                .antMatchers(HttpMethod.POST, "/users")
                .antMatchers(HttpMethod.GET, "/users/verify")
                .antMatchers(HttpMethod.PUT, "/users/re-verify")
                .antMatchers(HttpMethod.PUT, "/users/activate");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/oauth/token")
            .permitAll()
            .anyRequest()
            .authenticated();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(authenticationProvider());
    }
}
