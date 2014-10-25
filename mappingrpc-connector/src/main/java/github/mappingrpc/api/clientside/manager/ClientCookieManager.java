package github.mappingrpc.api.clientside.manager;

import github.mappingrpc.api.clientside.domain.Cookie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class ClientCookieManager {

	private volatile String cookieCacheForReturnToServer = "[]";
	private volatile Calendar cookieCacheExpiredTime;
	private volatile boolean needFlushToDisk = false;
	private Map<String, Cookie> memoryCookieStore = new ConcurrentHashMap<String, Cookie>();
	
	private CookieStoreManager storeManager;

	public ClientCookieManager(CookieStoreManager cookieStoreManager) {
		this.storeManager = cookieStoreManager;
	}
	
	public void start(){
		cookieCacheForReturnToServer = storeManager.loadCookieFromStore();
		List<Cookie> cookieList = JSONArray.parseArray(cookieCacheForReturnToServer, Cookie.class);
		for(Cookie cookie : cookieList){
			memoryCookieStore.put(cookie.getName(), cookie);
		}
	}

	public void processSetCookie(Cookie[] cookieList) {
		for (Cookie cookie : cookieList) {
			if (cookie.getMaxAge() == 0) {
				memoryCookieStore.remove(cookie.getName());
				continue;
			}
			if (cookie.getMaxAge() > 0) {
				memoryCookieStore.put(cookie.getName(), cookie);
				Calendar expiredTime = Calendar.getInstance();
				expiredTime.add(Calendar.SECOND, cookie.getMaxAge());
				if (expiredTime.before(cookieCacheExpiredTime)) {
					cookieCacheExpiredTime = expiredTime;
				}
			}
		}
		this.cookieCacheForReturnToServer = generateCallCmdCookieJson();
		needFlushToDisk = true;
	}

	public void delCookie(List<String> cookieNameList) {
		for (String name : cookieNameList) {
			memoryCookieStore.remove(name);
		}
		this.cookieCacheForReturnToServer = memoryCookieStore.toString();
		needFlushToDisk = true;
	}

	public String getCookieForSendToServer() {
		Calendar nowTime = Calendar.getInstance();
		if (nowTime.after(cookieCacheExpiredTime)) {
			cookieCacheForReturnToServer = generateCallCmdCookieJson();
		}
		return cookieCacheForReturnToServer;
	}

	public void flushCookieToDisk() {
		if (needFlushToDisk) {
			List<Cookie> cookieList = new ArrayList<Cookie>();
			for(Cookie cookie : memoryCookieStore.values()){
				if(cookie.getMaxAge() > 0){
					cookieList.add(cookie);
				}
			}
			storeManager.flushCookieToStore(JSON.toJSONString(cookieList.toArray()));
			needFlushToDisk = false;
		}
	}

	private String generateCallCmdCookieJson() {
		List<Cookie> cookieList = new ArrayList<Cookie>();
		Iterator<Cookie> it = memoryCookieStore.values().iterator();
		for (; it.hasNext();) {
			Cookie cookie = it.next();
			Calendar nowTime = Calendar.getInstance();
			if (nowTime.after(cookieCacheExpiredTime)) {
				it.remove();
			} else {
				cookieList.add(cookie);
			}
		}
		return JSON.toJSONString(cookieList.toArray());
	}
}
