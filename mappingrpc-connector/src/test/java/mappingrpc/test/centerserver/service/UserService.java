package mappingrpc.test.centerserver.service;

import github.mappingrpc.api.annotation.RequestMapping;

import com.github.mappingrpc.connector.test.domain.User;
import com.github.mappingrpc.connector.test.domain.option.RegisterOption;

public interface UserService {
	
	@Deprecated
	@RequestMapping("/userservice/register/20140305/")
	public User registerUser(User user, String password);

	@RequestMapping("/userService/registerUser/v20140308/")
	public User registerUser(User user, String password, RegisterOption option);
}
