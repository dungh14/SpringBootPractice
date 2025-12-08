package vn.dungjava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.dungjava.controller.AuthenticationController;
import vn.dungjava.controller.EmailController;
import vn.dungjava.controller.UserController;

@SpringBootTest
class BackendServiceApplicationTests {

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private AuthenticationController authenticationController;

	@InjectMocks
	private EmailController emailController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(userController);
		Assertions.assertNotNull(authenticationController);
		Assertions.assertNotNull(emailController);
	}

}
