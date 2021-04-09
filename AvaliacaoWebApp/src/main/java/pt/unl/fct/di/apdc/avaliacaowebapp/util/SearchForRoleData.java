package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class SearchForRoleData {
	
	
	public String role;
	public AuthToken token;

	public SearchForRoleData() {}
	
	
	
	
	public SearchForRoleData(AuthToken token, String role) {
		this.token = token;
		this.role = role;
	}



	public boolean validSearchForRole() {
		if(token == null || role == null)
			return false;
		
		if(!role.equals("USER") && !role.equals("GBO") && !role.equals("GA") && !role.equals("SU") )
			return false;
		
		return true;
	}

}
