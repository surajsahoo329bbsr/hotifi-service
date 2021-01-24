package com.api.hotifi.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "api-hotifi.oauth")
public class OAuthProperties {

    private Token token = new Token();
    private Token refreshToken = new Token();
    private String client;
    private String secret;
    private List<String> grantTypes = new ArrayList<>();
    private List<String> scopes = new ArrayList<>();

    @Getter
    @Setter
    public static class Token {
        private int timeout = 3600;
    }

}
