using System;
using mappingrpc.cookie;

namespace mappingrpc.cookie
{
	public class CookieToServer
	{
		public string name;
		public string value;

		public CookieToServer(Cookie cookie){
			this.name = cookie.name;
			this.value = cookie.value;
		}
	}
}

