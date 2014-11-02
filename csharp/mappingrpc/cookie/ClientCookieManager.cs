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
		private Dictionary<string, Cookie> memoryCookieStore = new Dictionary<string, Cookie>();

		private CookieStoreManager storeManager;

		public ClientCookieManager(CookieStoreManager cookieStoreManager) {
			this.storeManager = cookieStoreManager;
		}

		public void start(){
			cookieCacheForReturnToServer = storeManager.loadCookieFromStore();
			List<Cookie> cookieList = JsonConvert.DeserializeObject<List<Cookie>>(cookieCacheForReturnToServer);
			foreach(Cookie cookie in cookieList){
				memoryCookieStore.Add(cookie.name, cookie);
			}
		}

		public void processSetCookie(Cookie[] cookieList) {
			foreach (Cookie cookie in cookieList) {
				if (cookie.maxAge == 0) {
					memoryCookieStore.Remove(cookie.name);
					continue;
				}
				if (cookie.maxAge > 0) {
					memoryCookieStore.Add(cookie.name, cookie);
					DateTime expiredTime = DateTime.Now;
					expiredTime = expiredTime.AddSeconds(cookie.maxAge);
					if (expiredTime < cookieCacheExpiredTime) {
						cookieCacheExpiredTime = expiredTime;
					}
				}
			}
			this.cookieCacheForReturnToServer = generateCallCmdCookieJson();
			needFlushToDisk = true;
		}

		public void delCookie(List<string> cookieNameList) {
			foreach(string name in cookieNameList) {
				memoryCookieStore.Remove(name);
			}
			this.cookieCacheForReturnToServer = memoryCookieStore.ToString();
			needFlushToDisk = true;
		}

		public String getCookieForSendToServer() {
			if (DateTime.Now > cookieCacheExpiredTime) {
				cookieCacheForReturnToServer = generateCallCmdCookieJson();
			}
			return cookieCacheForReturnToServer;
		}

		public void flushCookieToDisk() {
			if (needFlushToDisk) {
				List<Cookie> cookieList = new List<Cookie>();
				foreach(Cookie cookie in memoryCookieStore.Values){
					if(cookie.maxAge > 0){
						cookieList.Add(cookie);
					}
				}
				storeManager.flushCookieToStore(JsonConvert.SerializeObject(cookieList));
				needFlushToDisk = false;
			}
		}

		private String generateCallCmdCookieJson() {
			List<Cookie> removeList = new List<Cookie>();
			foreach (Cookie cookie in memoryCookieStore.Values) {
				if (DateTime.Now > cookieCacheExpiredTime) {
					removeList.Add (cookie);
				}
			}
			foreach (Cookie cookie in removeList) {
				memoryCookieStore.Remove (cookie.name);
			}
			return JsonConvert.SerializeObject(memoryCookieStore.Values);
		}
	}
}

