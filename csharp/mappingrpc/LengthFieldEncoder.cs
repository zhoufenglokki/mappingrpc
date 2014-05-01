using System;
using Mina.Filter.Codec.Demux;
using Mina.Filter.Codec;
using Mina.Core.Session;
using mappingrpc.command;
using Mina.Core.Buffer;

namespace mappingrpc
{
	public class LengthFieldEncoder:IMessageEncoder<WampCommandBase>
	{

		public void Encode(IoSession session, WampCommandBase message, IProtocolEncoderOutput output){
			Console.WriteLine ("not run to here");
		}

		public void Encode(IoSession session, object message, IProtocolEncoderOutput output){
			WampCommandBase cmd = (WampCommandBase)message;
			string json = cmd.toCommandJson();
			byte[] jsonByteArray = System.Text.Encoding.UTF8.GetBytes(json);
			int len = jsonByteArray.Length + 4;
			if (len > 1000000) {
				throw new ArgumentException("{msg:'data size > 1m', dataSize:" + len + "}");
			}
			IoBuffer buffer = IoBuffer.Allocate(len);
			buffer.AutoExpand = true;
			buffer.PutInt32 (len);
			buffer.Put(jsonByteArray);

			buffer.Flip();
			output.Write (buffer);
		}
	}
}

