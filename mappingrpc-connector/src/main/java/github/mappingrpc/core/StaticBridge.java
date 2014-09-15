package github.mappingrpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticBridge {
	private static Map<String, CoreEngine> coreEngineList = new ConcurrentHashMap<>();
	
	public static void putCoreEngine(String beanName, CoreEngine coreEngine){
		coreEngineList.put(beanName, coreEngine);
	}
	
	public static void removeCoreEngine(String beanName){
		coreEngineList.remove(beanName);
	}
	
	public static CoreEngine queryFirstCoreEngine(){
		return coreEngineList.entrySet().iterator().next().getValue();
	}
}
