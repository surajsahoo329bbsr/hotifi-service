package com.api.hotifi;

import com.api.hotifi.identity.AuthenticationControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		//AccessTokenUtils.class, //test case 1
		AuthenticationControllerTest.class     //test case 2
})
//@SpringBootTest
class HotifiApplicationTests {

	//Using this as Suite Test To Control The Order Of Test Files

	@Test
	void contextLoads() {
	}

}
