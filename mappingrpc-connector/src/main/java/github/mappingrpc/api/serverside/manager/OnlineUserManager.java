package github.mappingrpc.api.serverside.manager;

import github.mappingrpc.core.StaticBridge;
import github.mappingrpc.core.session.SessionManager;

public class OnlineUserManager {
	static SessionManager sessionManager;

	//public void callOnlineUser() {}

	public static void setupClientSessionToSuccessLogin(Object sessionOpaque){
		
	}
	
	public static void listOnlineUser() {
		StaticBridge.queryFirstCoreEngine();
	}

}
