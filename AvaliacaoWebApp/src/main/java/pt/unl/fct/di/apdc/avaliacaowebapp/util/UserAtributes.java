package pt.unl.fct.di.apdc.avaliacaowebapp.util;

import java.util.UUID;

public class UserAtributes {
	
	
	public String username;
	public String email;
	public String role;
	public String perfil;
	//public String perfilState;
	public String telefoneFixo;
	public String telemovel;
	public String morada;
	public String moradaComplementar;
	public String localidade;
	public String cp;
	public String state;
	

	public UserAtributes() {
		// TODO Auto-generated constructor stub
	}
	
	public UserAtributes(String username, String email, String role, String perfil, String telefoneFixo, String telemovel, String morada, String moradaComplementar, String localidade, String cp, String state) {
		this.username = username;
		this.email = email;
		this.role = role;
		this.perfil = perfil;
		this.telefoneFixo = telefoneFixo;
		this.telemovel = telemovel;
		this.morada = morada;
		this.moradaComplementar = moradaComplementar;
		this.localidade = localidade;
		this.cp = cp;
		this.state = state;
		
		
	}
	
	public UserAtributes(String state) {
		this.state = state;
	}

}
