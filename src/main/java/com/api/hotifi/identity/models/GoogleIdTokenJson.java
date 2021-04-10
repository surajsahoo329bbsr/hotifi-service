package com.api.hotifi.identity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class GoogleIdTokenJson implements Serializable {

    private final String idToken;

}
