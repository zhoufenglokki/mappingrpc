using System;
using Mina.Transport.Socket;
using Mina.Filter.Codec;
using Newtonsoft.Json.Linq;
using Mina.Core.Session;
using System.Net;
using mappingrpc.command;
using Mina.Filter.Logging;
using Newtonsoft.Json;
using System.Threading;

namespace mappingrpc
{
	public class RpcClient
	{
		private string host;
		private short port;
		MetaHolder metaHolder = new MetaHolder ();
		AsyncSocketConnector connector;
		IoSession ioSession;

		public RpcClient (string host, short port)
		{
			this.host = host;
			this.port = port;
		}

		public void start ()
		{
			connector = new AsyncSocketConnector ();
			connector.FilterChain.AddLast ("codec", new ProtocolCodecFilter (new CustomPackageFactory ()));
			connector.FilterChain.AddLast ("logger", new LoggingFilter ());
			connector.MessageReceived += onMessageReceived;
			connector.SessionOpened += (sender, eventArgs) => {
				this.ioSession = eventArgs.Session;
				Console.WriteLine ("connected.");
			};
			Console.WriteLine ("connecting...");
			var addresses = System.Net.Dns.GetHostAddresses(host);
			var endPoint = new IPEndPoint(addresses[0], port);
			connector.Connect (endPoint);
			Thread.Sleep (800);
		}

		public void onMessageReceived (object sender, IoSessionMessageEventArgs eventArgs)
		{
			String json = (String)eventArgs.Message;
			JArray jsonArray = JArray.Parse (json);
			int commandType = jsonArray [0].Value<int> ();
			if (commandType == MsgTypeConstant.result) {
				long requestId = jsonArray [1].ToObject<int> ();
				CallResultFuture future = (CallResultFuture)metaHolder.requestPool [requestId];
				JArray resultArray = JArray.Parse (jsonArray [3].ToString());
				future.result = resultArray.First.ToObject(future.resultType);
				future.done = true;
				lock (future.monitorLock) {
					Monitor.PulseAll (future.monitorLock);
				}
			}
			// TODO 需要改为线程池处理
		}

		public T invoke<T> (string mappingUrl, params object[] paramList)
		{
			CallCommand callCmd = new CallCommand ();
			callCmd.procedureUri = mappingUrl;
			callCmd.args = paramList;
			CallResultFuture asyncResult = new CallResultFuture ();
			metaHolder.requestPool.Add (callCmd.requestId, asyncResult);
			asyncResult.resultType = typeof(T);
			ioSession.Write (callCmd);
			lock (asyncResult.monitorLock) {
				Monitor.Wait (asyncResult.monitorLock, 1000);
			}
			if (!asyncResult.done) {
				return default(T);
			}
			return (T)asyncResult.result;
			//return default(T);
		}
	}
}

