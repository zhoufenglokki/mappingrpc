using System;
using Mina.Filter.Codec.Demux;
using Mina.Core.Session;
using Mina.Filter.Codec;
using Mina.Core.Buffer;

namespace mappingrpc
{
	public class LengthFieldDecoder:IMessageDecoder
	{
		public LengthFieldDecoder ()
		{
		}

		public MessageDecoderResult Decodable (IoSession session, IoBuffer input)
		{
			if (input.Remaining < 4) {
				return MessageDecoderResult.NeedData;
			}
			int len = input.GetInt32 ();
			if (input.Remaining < len - 4) {
				return MessageDecoderResult.NeedData;
			}
			if (len > 1000000) {
				return MessageDecoderResult.NotOK;
			}
			return MessageDecoderResult.OK;
		}

		public MessageDecoderResult Decode (IoSession session, IoBuffer input, IProtocolDecoderOutput output)
		{
			int totalLen = input.GetInt32 ();
			int len = totalLen - 4;
			if (input.Remaining < len) {
				return MessageDecoderResult.NeedData;
			}
			byte[] jsonBuffer = new byte[len];
			input.Get (jsonBuffer, 0, len);
			string msg = System.Text.Encoding.UTF8.GetString(jsonBuffer);
			output.Write (msg);
			return MessageDecoderResult.OK;
		}

		public virtual void FinishDecode (IoSession session, IProtocolDecoderOutput output)
		{
		}
	}
}

