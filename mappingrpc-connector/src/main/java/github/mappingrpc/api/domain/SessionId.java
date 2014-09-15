package github.mappingrpc.api.domain;

import java.util.Map;

/**
 * 
 * no clientIp/clientPhoneNum for privacy policy
 * 
 * @author zhoufenglokki
 *
 */
public class SessionId {
	private String sessionId;
	private Map<String, String> cookie;
}
