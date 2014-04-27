package mappingrpc.test.testcase;

import mappingrpc.test.centerserver.service.UserService;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.github.mappingrpc.connector.test.domain.User;

@ContextConfiguration({"/localServerSide/spring-context.xml"})
public class LocalServerTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	UserService userService;

	@Test
	public void test() throws Exception {
		Assert.assertNotNull("注入", userService);
		User user = new User();
		user.setDisplayName("lokki");
		User result = userService.registerUser(user, "234");
		System.err.println(result.getDisplayName() + "\nid:" + result.getId());
		
		user.setDisplayName("zhoufeng");
		result = userService.registerUser(user, "789");
		System.err.println(result.getDisplayName() + "\nid:" + result.getId());
	}
}
