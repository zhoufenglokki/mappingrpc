using System;

namespace mappingrpc
{
	public class HostPort
	{
		public String host;
		public short port;
		public HostPort (String host, short port)
		{
			this.host = host;
			this.port = port;
		}
	}
}

