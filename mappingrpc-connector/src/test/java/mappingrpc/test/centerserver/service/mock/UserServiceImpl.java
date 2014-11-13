package mappingrpc.test.centerserver.service.mock;

import github.mappingrpc.api.annotation.RequestMapping;
import github.mappingrpc.api.clientside.domain.Cookie;
import github.mappingrpc.api.threadmodel.ServerCookieManager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mappingrpc.test.centerserver.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mappingrpc.connector.test.domain.User;
import com.github.mappingrpc.connector.test.domain.option.LoginOption;
import com.github.mappingrpc.connector.test.domain.option.RegisterOption;
import com.github.mappingrpc.connector.test.domain.result.ModelResult;

public class UserServiceImpl implements UserService {
	Logger log = LoggerFactory.getLogger(getClass());

	@Deprecated
	@RequestMapping("/userservice/register/20140305/")
	public User registerUser(User user, String password) {
		System.err.println("service:" + "/userservice/register/20140305/");
		System.err.println("userName:" + user.getDisplayName());
		user.setId((new Random()).nextLong());
		user.setDisplayName("centerserver_ok_" + user.getDisplayName());
		return user;
	}

	@Override
	@RequestMapping("/userService/registerUser/v20140308/")
	public User registerUser(User user, String password, RegisterOption option) {
		return user;
	}

	@Override
	@RequestMapping("/userService/loginMobile/v20141013/")
	public ModelResult<User> login(User user, String password, LoginOption option) {
		long uuid;
		try {
			uuid = SecureRandom.getInstanceStrong().nextLong();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		Cookie cookie = new Cookie();
		cookie.setName("loginToken");
		cookie.setValue(uuid + "");
		cookie.setMaxAge(3000);
		ServerCookieManager.attachCookieToUpStreamInDetail(cookie);
		return new ModelResult(user);
	}
	
	@Override
	@RequestMapping("/userService/loginNoGenericsResult/v20141110/")
	public User loginNoGenericsResult(User user, String password, LoginOption option) {
		long uuid;
		try {
			uuid = SecureRandom.getInstanceStrong().nextLong();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		Cookie cookie = new Cookie();
		cookie.setName("loginToken");
		cookie.setValue(uuid + "");
		cookie.setMaxAge(3000);
		ServerCookieManager.attachCookieToUpStreamInDetail(cookie);
		return user;
	}
}
