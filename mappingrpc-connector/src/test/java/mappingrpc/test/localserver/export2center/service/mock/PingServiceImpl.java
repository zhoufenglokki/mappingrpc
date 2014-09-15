package mappingrpc.test.localserver.export2center.service.mock;

import mappingrpc.test.localserver.export2center.service.PingService;

public class PingServiceImpl implements PingService {

	@Override
	public String echo(String msg) {
		return "echo:"+ msg;
	}

}
