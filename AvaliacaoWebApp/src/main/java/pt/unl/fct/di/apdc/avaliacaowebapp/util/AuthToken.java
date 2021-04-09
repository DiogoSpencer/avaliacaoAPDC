package pt.unl.fct.di.apdc.avaliacaowebapp.util;

import java.util.UUID;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;

public class AuthToken {
	public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2h
	public String username;
	public String tokenID;
	public String role;
	public long creationData;
	public long expirationData;
	
	
	
	
	public AuthToken() {}
	
	

	public AuthToken(String username, String role) {
		this.username = username;
		this.role = role;
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
		this.tokenID = UUID.randomUUID().toString();
		
	}
	
	
	public boolean isValid() {
		
		final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		
		Key tokenkey = datastore.newKeyFactory().setKind("Token").newKey(tokenID);
		Entity tokenEntity = datastore.get(tokenkey);
		if(tokenEntity == null) //token nao existe na datastore
			return false;
		else {
			String validUsername = tokenEntity.getString("token_username");
			String validTokenId = tokenEntity.getString("token_id");
			String validRole = tokenEntity.getString("token_role");
			long creationDate = tokenEntity.getLong("token_creationData");
			long expirationDate = tokenEntity.getLong("token_expirationData");
			if(tokenID.equals(validTokenId) && username.equals(validUsername) && role.equals(validRole) && creationData == creationDate && expirationData == expirationDate &&System.currentTimeMillis() < expirationDate)
				return true;
			else if(System.currentTimeMillis() >= expirationDate) { //apagar token experado
				datastore.delete(tokenkey);
				return false;
			}
			    
		}
		return false;
		
		
		
	}
	
}
