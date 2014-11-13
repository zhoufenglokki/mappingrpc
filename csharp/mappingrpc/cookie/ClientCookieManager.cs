using System;
using System.Collections;
using Newtonsoft.Json;
using mappingrpc.cookie;
using System.Collections.Generic;

namespace mappingrpc.clientside.cookie
{
	public class ClientCookieManager
	{
		private volatile string cookieCacheForReturnToServer = "[]";
		private DateTime cookieCacheExpiredTime;
		private volatile bool needFlushToDisk = false;
		private Dictionary<string, Cookie> memoryCookieStore = new Dictionary<string, Cookie> ();
		private CookieStoreManager storeManager;

		public ClientCookieManager (CookieStoreManager cookieStoreManager)
		{
			this.storeManager = cookieStoreManager;
		}

		public void start ()
		{
			memoryCookieStore.Clear ();
			string oldCookieContent = storeManager.loadCookieFromStore ();
			List<Cookie> cookieList = JsonConvert.DeserializeObject<List<Cookie>> (oldCookieContent);
			DateTime minExpiredTime = DateTime.MaxValue;
			foreach (Cookie cookie in cookieList) {
				if (cookie.maxAge == -1) {
					continue;
				}
				if (cookie.expiredTime < DateTime.Now) {
					continue;
				}
				if (cookie.expiredTime < minExpiredTime) {
					minExpiredTime = cookie.expiredTime;
				}
				memoryCookieStore.Add (cookie.name, cookie);
			}
			cookieCacheExpiredTime = minExpiredTime;
			cookieCacheForReturnToServer = JsonConvert.SerializeObject (memoryCookieStore.Values);
		}

		public void processSetCookie (Cookie[] cookieList)
		{
			DateTime minExpiredTime = cookieCacheExpiredTime;
			foreach (Cookie cookie in cookieList) {
				if (cookie.maxAge == 0) {
					memoryCookieStore.Remove (cookie.name);
					continue;
				}
				if (cookie.maxAge == -1) {
					memoryCookieStore[cookie.name] = cookie;
				}
				if (cookie.maxAge > 0) {
					memoryCookieStore[cookie.name] = cookie;
					DateTime expiredTime = DateTime.Now;
					expiredTime = expiredTime.AddSeconds (cookie.maxAge);
					cookie.expiredTime = expiredTime;
					if (expiredTime < minExpiredTime) {
						minExpiredTime = expiredTime;
					}
				}
			}
			this.cookieCacheForReturnToServer = JsonConvert.SerializeObject (memoryCookieStore.Values);
			needFlushToDisk = true;
			flushCookieToDisk (cookieCacheForReturnToServer);
		}
		/*
		public void delCookie(List<string> cookieNameList) {
			foreach(string name in cookieNameList) {
				memoryCookieStore.Remove(name);
			}
			this.cookieCacheForReturnToServer = memoryCookieStore.ToString();
			needFlushToDisk = true;
		}*/
		public List<CookieToServer> getCookieForSendToServer ()
		{
			List<Cookie> removeList = new List<Cookie> ();
			List<CookieToServer> toServerList = new List<CookieToServer>();
			foreach (Cookie cookie in memoryCookieStore.Values) {
				if (DateTime.Now > cookie.expiredTime) {
					removeList.Add (cookie);
				} else {
					toServerList.Add (new CookieToServer (cookie));
				}
			}
			foreach (Cookie cookie in removeList) {
				memoryCookieStore.Remove (cookie.name);
			}
			if (removeList.Count > 0) {
				this.needFlushToDisk = true;
				flushCookieToDisk ();
			}
			return toServerList;
		}

		private String generateCallCmdCookieJson ()
		{
			List<Cookie> removeList = new List<Cookie> ();
			foreach (Cookie cookie in memoryCookieStore.Values) {
				if (DateTime.Now > cookie.expiredTime) {
					removeList.Add (cookie);
				}
			}
			if (removeList.Count == 0) {
				return null;
			}
			foreach (Cookie cookie in removeList) {
				memoryCookieStore.Remove (cookie.name);
			}
			return JsonConvert.SerializeObject (memoryCookieStore.Values);
		}

		private void flushCookieToDisk (string content)
		{
			if (needFlushToDisk) {
				storeManager.flushCookieToStore (content);
				needFlushToDisk = false;
			}
		}

		public void flushCookieToDisk ()
		{
			if (needFlushToDisk) {
				List<Cookie> cookieList = new List<Cookie> ();
				foreach (Cookie cookie in memoryCookieStore.Values) {
					if (cookie.maxAge > 0) {		// TODO maxAge and now
						cookieList.Add (cookie);
					}
				}
				storeManager.flushCookieToStore (JsonConvert.SerializeObject (cookieList));
				needFlushToDisk = false;
			}
		}
	}
}

