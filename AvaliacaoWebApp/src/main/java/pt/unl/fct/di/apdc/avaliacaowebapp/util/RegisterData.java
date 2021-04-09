package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class RegisterData {

	
	
	public String username;
	public String password;
	public String passwordConfirmation;
	public String email;
	public AuthToken token;

	
	
	
	public RegisterData() {
	}
	
	public RegisterData(String username, String email, String password, String passwordConfirmation, AuthToken token) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.passwordConfirmation = passwordConfirmation;
		this.token = token;
	
		
		
	}

	public boolean validRegistration() {
		
		String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";   
		
		if(username == null)
			return false;
		if(password == null)
			return false;
		if(passwordConfirmation == null)
			return false;
		if(email == null)
			return false;
		if(!email.matches(regex))
			return false;
		if(!password.equals(passwordConfirmation))
			return false;
		
		return true;
	}
}
