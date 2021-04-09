package pt.unl.fct.di.apdc.avaliacaowebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaowebapp.util.AuthToken;
import pt.unl.fct.di.apdc.avaliacaowebapp.util.LoginData;
import pt.unl.fct.di.apdc.avaliacaowebapp.util.UserAtributes;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {


	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public LoginResource() {
	} // Nothing to be done here

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);
		
		
		
		

		Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Transaction txn = datastore.newTransaction();
		try {

			Entity user = txn.get(userkey);
			
			if(data.token != null) {
				if(data.token.isValid()){
					txn.rollback();
					LOG.warning("User is already logged in: " + data.username);
					return Response.status(Status.BAD_REQUEST).entity("You are already logged in, please logout if you want to do a new login.").build();
				}
			}

			
			if(user == null) {    //username does not exist
				txn.rollback();
				LOG.warning("Failed login attempt by user: " + data.username);
				return Response.status(Status.FORBIDDEN).entity("User '" + data.username + "' does not exist").build();
			}	
			String state = user.getString("user_state");
			if(state.equals("DISABLED")) {  //account is disabled
				txn.rollback();
				LOG.warning("This account is disabled: " + data.username);
				return Response.status(Status.FORBIDDEN).entity("User '" + data.username + "' is disabled").build();
				
			}
				
			
				String hashedPWD = user.getString("user_pwd");
				if(hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {

					AuthToken token = new AuthToken(data.username, user.getString("user_role"));
					UserAtributes atributes = null;
					String profileState = user.getString("user_profileState");
					if(profileState.equals("PUBLIC")) {
						atributes = new UserAtributes(user.getString("user_name"),
								user.getString("user_email"),
								user.getString("user_role"),
								user.getString("user_profile"),
								user.getString("user_housePhone"),
								user.getString("user_phone"),
								user.getString("user_adress1"),
								user.getString("user_adress2"),
								user.getString("user_local"),
								user.getString("user_postcode"),
								profileState);


					}else if(user.getString("user_profileState").equals("PRIVATE")) {
						atributes = new UserAtributes(profileState);
					}

					Key tokenkey = datastore.newKeyFactory().setKind("Token").newKey(token.tokenID);
					Entity tokenEntity = txn.get(tokenkey);
					if(tokenEntity != null) {
						txn.rollback();
						return Response.status(Status.BAD_REQUEST).entity("Token already exists").build();
					}else {
						tokenEntity = Entity.newBuilder(tokenkey)
								.set("token_id", token.tokenID)
								.set("token_username", token.username)
								.set("token_role", token.role)
								.set("token_creationData", token.creationData)
								.set("token_expirationData", token.expirationData)
								.build();
						txn.add(tokenEntity);
						LOG.info("User '" + data.username + "' logged in sucessfully.");
						txn.commit();
						return Response.ok(g.toJson(token) + g.toJson(atributes)).build();
					}
				}else {
					txn.rollback();
					LOG.warning("Wrong password for username: " + data.username);
					return Response.status(Status.FORBIDDEN).entity("Wrong password.").build();
				}

			
		}catch(Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}finally {
			if(txn.isActive())
				txn.rollback();
		}

	}


}