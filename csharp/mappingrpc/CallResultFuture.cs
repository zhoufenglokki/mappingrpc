using System;
using System.Threading;

namespace mappingrpc
{
	public class CallResultFuture
	{
		public Type resultType;
		public bool done = false;
		public bool isExceptionResult = false;
		public object result;
		public object monitorLock = new System.Object();

		public CallResultFuture(){
		}
		public object getResult(){
			lock (monitorLock) {
				Monitor.Wait (monitorLock);
			}
			return result;
		}

		public void putResult(object result){
			this.result = result;
			done = true;
			lock (monitorLock) {
				Monitor.PulseAll (monitorLock);
			}
		}
	}
}

