package mappingrpc.test.localserver.export2center.service;

import github.mappingrpc.api.annotation.RequestMapping;

public interface PingService {
	
	@RequestMapping("/pingservice/echo/20140528/")
	public String echo(String msg);
}
