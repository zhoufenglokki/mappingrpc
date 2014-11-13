using System;
using Newtonsoft.Json.Linq;
using System.Threading;
using System.Collections.Generic;

namespace mappingrpc.command
{
	public class CallCommand: WampCommandBase
	{
		public int msgType = MsgTypeConstant.call;
		public long requestId = 1;
		public Dictionary<string, object> options = new Dictionary<string, object> ();
		public string procedureUri;
		public object[] args;
		static long requestIdPool = 1;

		public CallCommand ()
		{
			requestId = Interlocked.Increment (ref requestIdPool);
		}

		public override object[] fieldToArray ()
		{
			return new Object[] { msgType, requestId, options, procedureUri, args };
		}

		public override string toCommandJson ()
		{
			return JArray.FromObject (fieldToArray ()).ToString ();
		}
	}
}

