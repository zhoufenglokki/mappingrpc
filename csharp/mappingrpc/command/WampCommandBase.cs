namespace mappingrpc.command
{
	public abstract class WampCommandBase
	{
		public abstract object[] fieldToArray ();

		public abstract string toCommandJson ();
	}
}