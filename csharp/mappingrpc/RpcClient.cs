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
using System.Collections.Generic;

namespace mappingrpc
{
	public class RpcClient
	{
		private string host;
		private short port;
		private IDictionary<string, short> serverList;

		Random rand = new Random ();
		MetaHolder metaHolder = new MetaHolder ();
		AsyncSocketConnector connector;
		IoSession ioSession;
		volatile int connecting = 0; 

		public RpcClient (string host, short port)
		{
			this.host = host;
			this.port = port;
		}

		public RpcClient(IDictionary<string, short> serverList){
			this.serverList = serverList;
		}

		public void start ()
		{
			makeConnectionInCallerThread (1);
		}

		private void makeConnectionInCallerThread(int waitInMs) {
			if (ioSession != null) {
				if (ioSession.Connected) {
					return;
				}
			}
			if (connecting == 1) {
				return;
			}
			int orgValue = Interlocked.CompareExchange (ref connecting, 1, 0);
			if (orgValue == 1) {
				return;
			}

			int serverIndex = rand.Next (serverList.Count);

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
			try{
				connector.Connect (endPoint).Await (waitInMs);
			}finally{
				connecting = 0;
			}
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
				future.isExceptionResult = false;
				lock (future.monitorLock) {
					Monitor.PulseAll (future.monitorLock);
				}
				return;
			}
			if (commandType == MsgTypeConstant.error) {// TODO
				long requestId = jsonArray [2].ToObject<int> ();
				CallResultFuture future = (CallResultFuture)metaHolder.requestPool [requestId];
				future.done = true;
				future.isExceptionResult = true;
				lock (future.monitorLock) {
					Monitor.PulseAll (future.monitorLock);
				}
				return;
			}
			// TODO 需要改为线程池处理
		}

		public T invoke<T> (int timeoutInMs, string mappingUrl, params object[] paramList)
		{
			makeConnectionInCallerThread (15000);
			if (ioSession == null || !ioSession.Connected) {
				return default(T);
			}
			CallCommand callCmd = new CallCommand ();
			callCmd.procedureUri = mappingUrl;
			callCmd.args = paramList;
			CallResultFuture asyncResult = new CallResultFuture ();
			metaHolder.requestPool.Add (callCmd.requestId, asyncResult);
			asyncResult.resultType = typeof(T);
			ioSession.Write (callCmd);
			lock (asyncResult.monitorLock) {
				Monitor.Wait (asyncResult.monitorLock, timeoutInMs);
			}
			if (!asyncResult.done) {
				throw new TimeoutException("{timeoutInMs:" + timeoutInMs +'}');
			}
			if (!asyncResult.isExceptionResult) {
				return (T)asyncResult.result;
			}
			if (asyncResult.isExceptionResult) {
				throw new Exception ((string)asyncResult.result);
			}
			return default(T);
		}
	}
}

