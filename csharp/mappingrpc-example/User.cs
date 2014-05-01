using System;

namespace mappingrpcexample
{
	public class User
	{
		private long id;
		private string displayName;
		private string password;

		public long Id {
			get {
				return this.id;
			}
			set {
				id = value;
			}
		}

		public string DisplayName {
			get {
				return this.displayName;
			}
			set {
				displayName = value;
			}
		}

		public string Password {
			get {
				return this.password;
			}
			set {
				password = value;
			}
		}
	}
}

