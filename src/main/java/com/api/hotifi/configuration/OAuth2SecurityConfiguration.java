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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class OAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /*@Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthenticationRepository authenticationRepository;*/


    // ignore all public API's
    //  "GET /categories", "GET /categories/{slugs}", "GET /products/category/{category-slug}/{subcategory-slugs}"
    // "GET /products/category/{slugs}", "GET /products/latest", "GET /products/{slugs}"
    /*@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                //.antMatchers("/v2/api-docs", "/configuration/ui",
                       // "/swagger-resources/**", "/configuration/**",
                       // "/swagger-ui.html", "/webjars/**")
                //.antMatchers(HttpMethod.GET, "/authenticate/get/access/**")
                /*.antMatchers(HttpMethod.GET, "/categories/**")
                .antMatchers(HttpMethod.GET, "/products/category/**")
                .antMatchers(HttpMethod.GET, "/products/category/**")
                .antMatchers(HttpMethod.GET, "/products/latest")
                .antMatchers(HttpMethod.GET, "/products/**")
                .antMatchers(HttpMethod.POST, "/users")
                .antMatchers(HttpMethod.GET, "/users/verify")
                .antMatchers(HttpMethod.PUT, "/users/re-verify")
                .antMatchers(HttpMethod.PUT, "/users/activate");
    }*/

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .anonymous()
            .disable()
            .authorizeRequests()
            .anyRequest()
            .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("john.carnell"). password("{noop}password1").roles("USER")
                .and()
                .withUser("william.woodward").password("{noop}password2").roles("USER", "ADMIN");}

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(Constants.SIGNING_KEY);
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    public OAuth2AccessToken getAccessToken(String email, String clientId, String token) {
        HashMap<String, String> authorizationParameters = new HashMap<>();
        String clientIdOrg = "eW91cl9jbGllbnRfaWQ6eW91cl9jbGllbnRfc2VjcmV0";
        if(!clientId.equals(clientIdOrg)) return null;
        authorizationParameters.put("scope", "read");
        authorizationParameters.put("username", email);
        authorizationParameters.put("client_id", clientId);
        authorizationParameters.put("grant_type", token);

        Authentication authentication = authenticationRepository.findByEmail(email);

        if(authentication == null) return null;
        if(!BCrypt.checkpw(authentication.getToken(), token)) return null;

        Set<GrantedAuthority> authorities = authentication.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toSet());

        Set<String> responseType = new HashSet<>();
        responseType.add(token);

        Set<String> scopes = new HashSet<>();
        scopes.add("read");
        scopes.add("write");

        OAuth2Request authorizationRequest = new OAuth2Request(authorizationParameters, clientId, authorities, true,
                scopes, null, "", responseType, null);

        org.springframework.security.core.userdetails.User userPrincipal = new org.springframework.security.core.userdetails.User(
                email, token, authorities);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal,
                null, authorities);

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(authorizationRequest,
                authenticationToken);
        authenticationRequest.setAuthenticated(true);

        return tokenServices().createAccessToken(authenticationRequest);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(false);
        return defaultTokenServices;
    }

    @Bean
    @Autowired
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
        handler.setClientDetailsService(clientDetailsService);
        return handler;
    }

    @Bean
    @Autowired
    public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }

    @Primary
    @Bean
    public UserDetailsService userDetailsService(AuthenticationRepository authenticationRepository) {
        return new AuthenticationServiceImpl(authenticationRepository);
    }*/

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
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
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
