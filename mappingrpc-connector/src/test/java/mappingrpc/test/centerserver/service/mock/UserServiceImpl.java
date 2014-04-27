package mappingrpc.test.centerserver.service.mock;

import github.mappingrpc.api.annotation.RequestMapping;

import java.util.Random;

import mappingrpc.test.centerserver.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mappingrpc.connector.test.domain.User;
import com.github.mappingrpc.connector.test.domain.option.RegisterOption;

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
}
