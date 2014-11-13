package mappingrpc.test.centerserver.service;

import github.mappingrpc.api.annotation.RequestMapping;

import com.github.mappingrpc.connector.test.domain.User;
import com.github.mappingrpc.connector.test.domain.option.LoginOption;
import com.github.mappingrpc.connector.test.domain.option.RegisterOption;
import com.github.mappingrpc.connector.test.domain.result.ModelResult;

public interface UserService {
	
	@Deprecated
	@RequestMapping("/userservice/register/20140305/")
	public User registerUser(User user, String password);

	@RequestMapping("/userService/registerUser/v20140308/")
	public User registerUser(User user, String password, RegisterOption option);
	
	@RequestMapping("/userService/loginMobile/v20141013/")
	public ModelResult<User> login(User user, String password, LoginOption option);
	
	@RequestMapping("/userService/loginNoGenericsResult/v20141110/")
	public User loginNoGenericsResult(User user, String password, LoginOption option);
}
