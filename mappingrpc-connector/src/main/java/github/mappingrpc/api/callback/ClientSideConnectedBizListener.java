package github.mappingrpc.api.callback;

import java.util.EventListener;

public interface ClientSideConnectedBizListener extends EventListener {
	public void connectedBizCallback();
}
