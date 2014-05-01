using System;
using mappingrpc;
using System.Threading;

namespace mappingrpcexample
{
	public class UserServiceClient
	{
		private RpcClient rpcClient;

		public UserServiceClient ()
		{
			rpcClient = new RpcClient ("localhost", 6200);
			rpcClient.start ();
		}

		public User registerUser (User user, string password)
		{
			return rpcClient.invoke<User> ("/userservice/register/20140305/", user, password);
		}
	}
}

