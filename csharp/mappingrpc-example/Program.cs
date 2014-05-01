using System;
using System.Threading;

namespace mappingrpcexample
{
	class MainClass
	{
		public static void Main (string[] args)
		{
			UserServiceClient userServiceClient = new UserServiceClient ();
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
