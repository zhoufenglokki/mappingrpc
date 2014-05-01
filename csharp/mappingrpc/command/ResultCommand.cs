using System;
using Newtonsoft.Json.Linq;

namespace mappingrpc.command
{
	public class ResultCommand: WampCommandBase
	{
		public int msgType = MsgTypeConstant.result;
		public long requestId = 0;
		public string details = "{}";
		public Object[] yieldResult = new Object[1];

		public override Object[] fieldToArray ()
		{
			return new Object[] { msgType, requestId, details, yieldResult };
		}

		public override string toCommandJson ()
		{
			return JArray.FromObject (fieldToArray ()).ToString ();
		}
	}
}

