package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class AccountStateData {
	
	
	public String userIdOfAccountToChange;
	public AuthToken token;

	public AccountStateData() {}
	
	
	
	
	public AccountStateData( String userIdOfAccountToChange, AuthToken token ) {
		this.userIdOfAccountToChange = userIdOfAccountToChange;
		this.token = token;
	}
	
	
	public boolean validModification() {
	     
		
		if(token == null)
			return false;
		if(userIdOfAccountToChange == null)
			return false;
		
		return true;
		
	}

}
