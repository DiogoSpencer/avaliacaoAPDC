package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class ModifyRoleData {


	public String usernameToModifyRole;
	public String newRole;
	public AuthToken token;

	public ModifyRoleData() {
		// TODO Auto-generated constructor stub
	}


	public ModifyRoleData(AuthToken token, String usernameToModifyRole, String newRole) {
		this.token = token;
		this.usernameToModifyRole = usernameToModifyRole;
		this.newRole = newRole;
	}



	public boolean validRoleModification() {
		if(token == null || usernameToModifyRole == null)
			return false;
		if(!newRole.equals("GBO") && !newRole.equals("GA"))
			return false;
		return true;
	}


}
