package com.api.hotifi;

import com.api.hotifi.controllers.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		AuthenticationControllerTest.class,     //test case 1
		UserControllerTest.class,     //test case 2
		SpeedTestControllerTest.class,     //test case 3
		BankAccountControllerTest.class,     //test case 4
		SessionControllerTest.class,     //test case 5
		PurchaseControllerTest.class     //test case 6
})
//@SpringBootTest
class HotifiApplicationTests {

	//Using this as Suite Test To Control The Order Of Test Files

	@Test
	void contextLoads() {
	}

}
