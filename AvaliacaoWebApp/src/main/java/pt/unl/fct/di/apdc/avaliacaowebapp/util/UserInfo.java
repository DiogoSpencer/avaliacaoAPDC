package pt.unl.fct.di.apdc.avaliacaowebapp.util;

import java.util.UUID;

import com.google.cloud.Timestamp;



public class UserInfo {
	
	
	public String username;
	public String email;
	public String password;
	public String role;
	public String perfil;
	public String perfilState;
	public String telefoneFixo;
	public String telemovel;
	public String morada;
	public String moradaComplementar;
	public String localidade;
	public String cp;
	public String state;
	public Timestamp creationDate;
	

	public UserInfo() {}
	
	
	public UserInfo(String username, String email,  String password, String role, String perfil, String perfilState, String telefoneFixo, String telemovel, String morada, String moradaComplementar, String localidade, String cp, String state, Timestamp creationDate) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.perfil = perfil;
		this.perfilState = perfilState;
		this.telefoneFixo = telefoneFixo;
		this.telemovel = telemovel;
		this.morada = morada;
		this.moradaComplementar = moradaComplementar;
		this.localidade = localidade;
		this.cp = cp;
		this.state = state;
		this.creationDate = creationDate;
		
		
		
		
	}
	
	public UserInfo(String state) {
		this.state = state;
	}

}
