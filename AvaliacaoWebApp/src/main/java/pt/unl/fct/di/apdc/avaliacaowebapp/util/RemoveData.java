package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class RemoveData {
	
	
	public String userToDelete;
	public AuthToken token;

	public RemoveData() {
		// TODO Auto-generated constructor stub
	}
    
	
	
	public RemoveData(AuthToken token, String userToDelete) {
		this.token = token;
		this.userToDelete = userToDelete;
		
	}
}
