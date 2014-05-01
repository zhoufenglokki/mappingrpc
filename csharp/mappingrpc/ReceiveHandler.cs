using System;

namespace mappingrpc
{
	public class ReceiveHandler
	{
		byte[] receiveBuffer;
		int bufferLen = 0;
		public ReceiveHandler (int maxPackageSize)
		{
			receiveBuffer = new byte[maxPackageSize];
		}
	}
}

