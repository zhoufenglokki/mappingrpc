package mappingrpc.test.testcase;

import mappingrpc.test.centerserver.service.UserService;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.github.mappingrpc.connector.test.domain.User;
import com.github.mappingrpc.connector.test.domain.option.LoginOption;
import com.github.mappingrpc.connector.test.domain.result.ModelResult;

@ContextConfiguration({"/cookieTestcase/test1/spring-context.xml"})
public class AppSideCookieTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	UserService userService;

	@BeforeClass
	public static void beforeClass(){
		System.err.println("beforeClass");
	}

	@Test
	public void test() throws Exception{
		Assert.assertNotNull("注入", userService);
		User user = new User();
		user.setDisplayName("lokki");
		User result = userService.registerUser(user, "234");
		System.err.println(result.getDisplayName() + "\nid:" + result.getId());
		
		//Thread.sleep(60000);
		
		user.setDisplayName("zhoufeng");
		LoginOption option = new LoginOption();
		ModelResult<User> loginResult = userService.login(user, "234", option);
		System.err.println("login:" + loginResult.getModel().getDisplayName());
		Thread.sleep(3000);

		loginResult = userService.login(user, "237", option);
		Thread.sleep(3000);
	}
}
