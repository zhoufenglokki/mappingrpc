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
			siteConfig.Add (SiteConfigConstant.key_cookieConnectionName, "example");
			RpcClientInstanceHolder.start (siteConfig);
			Thread.Sleep (50);

			login ();
	
			Thread.Sleep (1000);
		}

		static void login(){
			UserServiceClient userServiceClient = new UserServiceClient ();

			User user = new User();
			user.Id = 983;
			user.DisplayName = "happy";

			user = userServiceClient.login(user, "gxese", new LoginOption());
			Console.WriteLine ("login1");

			user = userServiceClient.login(user, "233uov", new LoginOption());
			Console.WriteLine ("login2");
		}

		static void registerUser(){
			UserServiceClient userServiceClient = new UserServiceClient ();

			User user = new User();
			user.DisplayName = "happy";
			user = userServiceClient.registerUser(user, "6237");
			Console.WriteLine ("return displayName:" + user.DisplayName);

			user.DisplayName = "nokia";
			user = userServiceClient.registerUser(user, "4433");
			Console.WriteLine ("return displayName:" + user.DisplayName);
		}
	}
}
