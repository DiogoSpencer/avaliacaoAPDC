package pt.unl.fct.di.apdc.avaliacaowebapp.util;

public class ModifyData {



	public String newPassword;
	public String password;
	public String newPasswordConfirmation;
	public String email;
	public String perfil;
	public String perfilState;
	public String telefoneFixo;
	public String telemovel;
	public String morada;
	public String moradaComplementar;
	public String localidade;
	public String cp;
	public AuthToken token;


	public ModifyData() {}





	public ModifyData( String email, String password, String perfil, String perfilState, String telefoneFixo, String telemovel, String morada, String moradaComplementar, String localidade,  String cp, AuthToken token, String newPassword, String newPasswordConfirmation) {
		
		this.email = email;
		this.password = password;
		this.perfil = perfil;
		this.perfilState = perfilState;
		this.telefoneFixo = telefoneFixo;
		this.telemovel = telemovel;
		this.morada = morada;
		this.moradaComplementar = moradaComplementar;
		this.localidade = localidade;
		this.cp = cp;
		this.token = token;
		this.newPassword = newPassword;
		this.newPasswordConfirmation = newPasswordConfirmation;


	}
	
	
public boolean validModification() {
		
		String regexEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
		String regexPhone = "^[0-9]{9}$";
		String regexPhone2 = "^\\+[0-9]{12}$";
		String regexCp = "^[0-9]{4}+\\-[0-9]{3}$";
		
		if(token == null)
			return false;
		if(email == null && password == null && perfil == null && perfilState == null && telefoneFixo == null && telemovel == null && morada == null && moradaComplementar == null && localidade == null && cp == null && newPassword == null && newPasswordConfirmation == null)
		    return false;
		
		if(perfilState != null)
			if(!perfilState.equals("PUBLIC") && !!perfilState.equals("PRIVATE"))
		           return false;
		
		if(email != null)
		   if(!email.matches(regexEmail))
			    return false;
		
		if(telemovel != null)
		     if(!telemovel.matches(regexPhone) && !telemovel.matches(regexPhone2))
			   return false;
		
		if(telefoneFixo != null)
		    if(!telefoneFixo.matches(regexPhone) && !telefoneFixo.matches(regexPhone2))
			    return false;
		
		if(cp != null)
		    if(!cp.matches(regexCp))
			   return false;
		
		if(newPassword != null) {
			if(password == null || !newPassword.equals(newPasswordConfirmation))
				return false;
			
		}
		
		
		
		return true;
	}
	

}
