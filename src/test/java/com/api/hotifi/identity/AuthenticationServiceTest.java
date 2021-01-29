package com.api.hotifi.identity;

import com.api.hotifi.identity.repositories.AuthenticationRepository;
import com.api.hotifi.identity.repositories.RoleRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;


@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private RoleRepository roleRepository;


    private final Map<String, Boolean> emailMap = Map.ofEntries(
            Map.entry("johnStuart56@gamil.com", true),
            Map.entry("sahooasish65@gmail.com", false),
            Map.entry("jackmafia5@gmail.com", true),
            Map.entry("jackreacher@gmail.com", true),
            Map.entry("hizenburg12@gmail.com", false),
            Map.entry("pinkmanMeth65@gmail.com", true),
            Map.entry("notantman@gmail.com", true),
            Map.entry("dasasish65@gmail.com", true),
            Map.entry("sahoobikas5@gmail.com", false),
            Map.entry("sahoosagyan20@gmail.com", true)
    );

    @Test
    @DisplayName("Testing add email")
    public void shouldAddEmail(){
        emailMap.forEach((email, isEmailVerified) ->{
        });
    }

}
