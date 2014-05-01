using NUnit.Framework;
using System;
using System.Threading;

namespace mappingrpcexample
{
	[TestFixture()]
	public class CsharpClientTest
	{
		[Test()]
		public void test ()
		{
			UserServiceClient userServiceClient = new UserServiceClient ();
			User user = new User();
			user.DisplayName = "happy";
			userServiceClient.registerUser(user, "6237");
		}
	}
}

