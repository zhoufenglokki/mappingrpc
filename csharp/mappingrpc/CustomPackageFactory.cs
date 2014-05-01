using System;
using Mina.Filter.Codec.Demux;
using mappingrpc.command;

namespace mappingrpc
{
	public class CustomPackageFactory:DemuxingProtocolCodecFactory
	{
		public CustomPackageFactory ()
		{
			AddMessageDecoder<LengthFieldDecoder>();
			AddMessageEncoder<WampCommandBase>(new LengthFieldEncoder());
			//AddMessageEncoder<WampCommandBase, LengthFieldEncoder<WampCommandBase>> ();
		}
	}
}

