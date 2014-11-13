using System;
using mappingrpc;
using System.Threading;
using System.Collections.Generic;

namespace mappingrpcexample
{
	public class UserServiceClient
	{
		public User registerUser (User user, string password)
		{
			return RpcClientInstanceHolder.rpcClient.invoke<User> (1500, "/userservice/register/20140305/", user, password);
		}

		public User login(User user, String password, LoginOption option){
			return RpcClientInstanceHolder.rpcClient.invoke<User> (1500, "/userService/loginNoGenericsResult/v20141110/", user, password, option);
		}
	}
}

