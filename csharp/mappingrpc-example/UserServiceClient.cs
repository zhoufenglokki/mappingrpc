using System;
using mappingrpc;
using System.Threading;
using System.Collections.Generic;

namespace mappingrpcexample
{
	public class UserServiceClient
	{
		private RpcClient rpcClient;

		public UserServiceClient ()
		{
			HostPort serverConfig = new HostPort ("localhost", 6200);
			IList<HostPort> serverList = new List<HostPort> ();
			serverList.Add (serverConfig);
			rpcClient = new RpcClient (serverList);
			rpcClient.start ();
		}

		public User registerUser (User user, string password)
		{
			return rpcClient.invoke<User> (1500, "/userservice/register/20140305/", user, password);
		}
	}
}

