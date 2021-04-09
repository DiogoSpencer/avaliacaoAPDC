package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class LoginData {
	
	public String username;
	public String password;
	public AuthToken token;
	
	public LoginData() {}
	
	public LoginData(String username, String password, AuthToken token) {
		this.username = username;
		this.password = password;
		this.token = token;
		
	}

}
