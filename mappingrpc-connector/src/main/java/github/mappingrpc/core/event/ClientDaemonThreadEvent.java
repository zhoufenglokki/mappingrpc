package github.mappingrpc.core.event;

import github.mappingrpc.core.constant.ClientDaemonThreadEventType;

public class ClientDaemonThreadEvent {
	private byte eventType = ClientDaemonThreadEventType.noEvent;

	public ClientDaemonThreadEvent(byte eventType) {
		this.eventType = eventType;
	}

	public byte getEventType() {
		return eventType;
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}
}
