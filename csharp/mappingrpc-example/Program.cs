using System;
using System.Threading;
using System.IO;
using System.Reflection;
using System.Collections.Generic;
using mappingrpc;

namespace mappingrpcexample
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
			System.Console.WriteLine (path);

			Dictionary<String, object> siteConfig = new Dictionary<String, object> ();
			siteConfig.Add (SiteConfigConstant.key_cookieSavePath, path + "/myappConfig");
			RpcClientInstanceHolder.start (siteConfig);

			UserServiceClient userServiceClient = new UserServiceClient ();
			Thread.Sleep (50);
			User user = new User();
			user.DisplayName = "happy";
			user = userServiceClient.registerUser(user, "6237");
			Console.WriteLine ("return displayName:" + user.DisplayName);

			user.DisplayName = "nokia";
			user = userServiceClient.registerUser(user, "4433");
			Console.WriteLine ("return displayName:" + user.DisplayName);
			Thread.Sleep (1000);
		}
	}
}
