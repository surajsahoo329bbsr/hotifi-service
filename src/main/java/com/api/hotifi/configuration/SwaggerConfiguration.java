package com.api.hotifi.configuration;

import com.api.hotifi.common.constant.Constants;
import com.fasterxml.classmate.TypeResolver;
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
                .enableUrlTemplating(false);
    }

    private Predicate<String> applicationPaths() {
        Predicate<String> authentications = PathSelectors.regex("/authenticate.*")::apply;
        Predicate<String> devices = PathSelectors.regex("/device.*")::apply;
        Predicate<String> users = PathSelectors.regex("/user.*")::apply;
        Predicate<String> userStatuses = PathSelectors.regex("/user-status.*")::apply;
        Predicate<String> feedbacks = PathSelectors.regex("/feedback.*")::apply;
        Predicate<String> purchases = PathSelectors.regex("/purchase.*")::apply;
        Predicate<String> sellerBankAccounts = PathSelectors.regex("/seller-bank-account.*")::apply;
        Predicate<String> sellerPayments = PathSelectors.regex("/seller-payment.*")::apply;
        Predicate<String> sellerReceipts = PathSelectors.regex("/seller-receipt.*")::apply;
        Predicate<String> stats = PathSelectors.regex("/stats.*")::apply;
        Predicate<String> speedTests = PathSelectors.regex("/speed-test.*")::apply;
        Predicate<String> sessions = PathSelectors.regex("/session.*")::apply;
        return authentications.or(devices).or(users).or(userStatuses)
                .or(feedbacks).or(purchases).or(sellerBankAccounts).or(sellerPayments)
                .or(sellerReceipts).or(stats).or(speedTests).or(sessions);
    }

    private ApiInfo apiInfo() {
        return info(String.format("%s Endpoints", Constants.APP_NAME));
    }

    private ApiInfo info(String title) {
        return new ApiInfo(title, "This page helps the user to try out the hotifi service endpoints in an more convenient manner", Constants.APP_VERSION, "https://www.hotifi.com/",
                new Contact(Constants.APP_NAME, null, "support@hotifi.com"),
                null,null, new ArrayList<>());
    }
}
