package github.mappingrpc.core.event;

import github.mappingrpc.core.constant.BossThreadEventType;

public class BossThreadEvent {
	private byte eventType = BossThreadEventType.noEvent;

	public BossThreadEvent(byte eventType) {
		this.eventType = eventType;
	}

	public byte getEventType() {
		return eventType;
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}
}
