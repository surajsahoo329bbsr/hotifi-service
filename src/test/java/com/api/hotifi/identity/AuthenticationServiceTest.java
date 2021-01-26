package com.api.hotifi.identity;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthenticationServiceTest {

    @Test
    @DisplayName("Testing add email")
    @BeforeAll()
    static void getAccessTokenBeforeInit(){
    }

    @Test
    @DisplayName("Testing add email")
    void testAddEmail(){

    }

}
