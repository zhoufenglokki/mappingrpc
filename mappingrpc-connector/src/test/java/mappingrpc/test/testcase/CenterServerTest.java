package mappingrpc.test.testcase;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration({"/centerServerSide/spring-context.xml"})
public class CenterServerTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() throws Exception {
		System.err.println("gatewayserver started");
		Thread.sleep(1000 * 60 * 100);
	}
}
