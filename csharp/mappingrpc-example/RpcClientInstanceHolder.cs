using System;
using mappingrpc;
using System.Collections.Generic;

namespace mappingrpcexample
{
	public class RpcClientInstanceHolder
	{
		public static RpcClient rpcClient;
		public static void start(Dictionary<String, object> siteConfig){
			HostPort serverConfig = new HostPort ("localhost", 6200);
			IList<HostPort> serverList = new List<HostPort> ();
			serverList.Add (serverConfig);
			rpcClient = new RpcClient (serverList);
			rpcClient.setSiteConfig (siteConfig);
			rpcClient.start ();
		}
	}
}

