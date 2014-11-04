using System;
using System.IO;

namespace mappingrpc.clientside.cookie
{
	public class CookieStoreManager
	{
		private string saveFilePath;

		public CookieStoreManager(string connectionName, string savePath) {
			saveFilePath = savePath + '/' + connectionName + "_cookie.json";
			Directory.CreateDirectory (savePath);
		}


		public String loadCookieFromStore() {
			if (!File.Exists (saveFilePath)) {
				return "[]";
			}
			StreamReader file = File.OpenText(saveFilePath);
			string content = file.ReadToEnd();
			file.Close();
			return content;
		}

		public void flushCookieToStore(string content) {
			StreamWriter file = new StreamWriter(saveFilePath);
				file.Write(content);
				file.Close();
		}
	}
}

