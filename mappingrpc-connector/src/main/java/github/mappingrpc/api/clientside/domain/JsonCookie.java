package github.mappingrpc.api.clientside.domain;

import com.alibaba.fastjson.JSONObject;

public class JsonCookie {
	private JSONObject memoryStore;

	public JSONObject getMemoryStore() {
		return memoryStore;
	}

	public void setMemoryStore(JSONObject memoryStore) {
		this.memoryStore = memoryStore;
	}
}
