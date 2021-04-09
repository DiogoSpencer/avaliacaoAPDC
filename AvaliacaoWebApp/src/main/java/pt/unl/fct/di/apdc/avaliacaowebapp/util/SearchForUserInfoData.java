package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class SearchForUserInfoData {
	
	
	public String username;
	public AuthToken token;

	public SearchForUserInfoData() {}
	
	
	
	
	public SearchForUserInfoData(AuthToken token, String username) {
		this.token = token;
		this.username = username;
	}



	public boolean validSearchForUserInfo() {
		if(token == null || username == null)
			return false;
		
		
		return true;
	}

}
