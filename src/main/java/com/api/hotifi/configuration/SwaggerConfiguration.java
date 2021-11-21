package com.api.hotifi.configuration;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.SwaggerDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Predicate;

@EnableSwagger2
@Configuration
@SwaggerDefinition
public class SwaggerConfiguration {

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket serviceDocket() {
        return getDocket(applicationPaths(), RequestHandlerSelectors.any()::apply, apiInfo());
    }

    private Docket getDocket(Predicate<String> pathPattern, Predicate<RequestHandler> apis,
                             ApiInfo apiinfo) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("hotifi-service")
                .select()
                .apis(apis::test)
                .paths(pathPattern::test)
                .build()
                .apiInfo(apiinfo)
                .pathMapping("/")
                .directModelSubstitute(LocalDate.class, String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(new AlternateTypeRule(
                        typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                //Uncomment below lines to turn on swagger authorize button ui
                //.securityContexts(Collections.singletonList(securityContext()))
                //.securitySchemes(Collections.singletonList(apiKey()))
                .enableUrlTemplating(false);
    }

    /*private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private OAuth securitySchema() {

        List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        authorizationScopeList.add(new AuthorizationScope("read", "read all"));
        authorizationScopeList.add(new AuthorizationScope("trust", "trust all"));
        authorizationScopeList.add(new AuthorizationScope("write", "access all"));

        List<GrantType> grantTypes = new ArrayList<>();
        GrantType credentialsGrant = new ResourceOwnerPasswordCredentialsGrant("http://localhost:8080/v1/oauth/token");

        grantTypes.add(credentialsGrant);

        return new OAuth("oauth2schema", authorizationScopeList, grantTypes);

    }

    private SecurityContext securityContext() {
        return SecurityContext
                .builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[3];
        authorizationScopes[0] = new AuthorizationScope("read", "read all");
        authorizationScopes[1] = new AuthorizationScope("trust", "trust all");
        authorizationScopes[2] = new AuthorizationScope("write", "write all");
        return Collections.singletonList(new SecurityReference("Oauth2Schema", authorizationScopes));
    }*/

    private Predicate<String> applicationPaths() {
        Predicate<String> authentications = PathSelectors.regex("/authenticate.*")::apply;
        Predicate<String> devices = PathSelectors.regex("/device.*")::apply;
        Predicate<String> users = PathSelectors.regex("/user.*")::apply;
        Predicate<String> userStatuses = PathSelectors.regex("/user-status.*")::apply;
        Predicate<String> feedbacks = PathSelectors.regex("/feedback.*")::apply;
        Predicate<String> bankAccounts = PathSelectors.regex("/bank-account.*")::apply;
        Predicate<String> purchases = PathSelectors.regex("/purchase.*")::apply;
        Predicate<String> sellerPayments = PathSelectors.regex("/payment.*")::apply;
        Predicate<String> stats = PathSelectors.regex("/stats.*")::apply;
        Predicate<String> speedTests = PathSelectors.regex("/speed-test.*")::apply;
        Predicate<String> sessions = PathSelectors.regex("/session.*")::apply;
        Predicate<String> emails = PathSelectors.regex("/email.*")::apply;
        Predicate<String> notifications = PathSelectors.regex("/notification.*")::apply;

        return authentications.or(devices).or(users).or(userStatuses)
                .or(feedbacks).or(bankAccounts).or(purchases)
                .or(sellerPayments).or(stats).or(speedTests).or(sessions).or(emails)
                .or(notifications);
    }

    private ApiInfo apiInfo() {
        return info(String.format("%s Endpoints", AppConfigurations.APP_NAME));
    }

    private ApiInfo info(String title) {
        return new ApiInfo(title, "This page helps the user to try out the hotifi service endpoints in an more convenient manner", AppConfigurations.APP_VERSION, "https://www.hotifi.com/",
                new Contact(AppConfigurations.APP_NAME, null, "hotifi.help@gmail.com"),
                null,null, new ArrayList<>());
    }
}
