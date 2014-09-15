package github.mappingrpc.api.serverside.domain.result;

import github.rpcappmodel.domain.result.BaseResult;

public class AuthResult extends BaseResult {
	private boolean userIdConfirmed = false;
	private boolean userUuidConfirmed = false;
	private boolean userAuthTypeConfirmed = false;
	private boolean userSessionIdConfirmed = false;

}
