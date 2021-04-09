package pt.unl.fct.di.apdc.avaliacaowebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaowebapp.util.AuthToken;
import pt.unl.fct.di.apdc.avaliacaowebapp.util.ModifyData;

@Path("/modify")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyResource {



	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();


	public ModifyResource() {}



	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modify(ModifyData data) {
		LOG.fine("Modify atrribute attempt by user: " + data.token.username);


		if(!data.token.isValid()) { //token nao e valido ou expirou
			return Response.status(Status.BAD_REQUEST).entity("Session expired, login again.").build();
		}
		
		
		

		if(!data.validModification()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong paramenter").build();
		}



		Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.token.username);
		Transaction txn = datastore.newTransaction();
		
		try {
		Entity user = txn.get(userkey);

		if(!data.token.role.equals("USER")) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity("This user does not have the role: USER").build();
		}


		if(user == null ) {  
			txn.rollback();
			LOG.warning("User does not exist");
			return Response.status(Status.FORBIDDEN).entity("User does not exist").build();

		}else {
			
			String state = user.getString("user_state");
			if(state.equals("DISABLED")) {  //account is disabled
				txn.rollback();
				LOG.warning("This account is disabled: " + data.token.username);
				return Response.status(Status.FORBIDDEN).entity("User '" + data.token.username + "' is disabled").build();
				
			}
			
			
			LOG.info("User '" + data.token.username + "'  modified is atributes.");
			if(data.email !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_email",data.email).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' email changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.newPassword != null) {

				String oldPassword = user.getString("user_pwd");

				if(DigestUtils.sha512Hex(data.password).equals(oldPassword)) {
					user= Entity.newBuilder(txn.get(userkey)).set("user_pwd",DigestUtils.sha512Hex(data.newPassword)).build();
					txn.update(user);
					LOG.info("User '" + data.token.username + "' password changed sucessfully.");
					txn.commit();
					txn = datastore.newTransaction();
					user = txn.get(userkey);
				}else {
					txn.rollback();
					LOG.warning("Wrong password");
					return Response.status(Status.BAD_REQUEST).entity("Wrong password").build();
				}
			}
			if(data.perfil !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_profile",data.perfil).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' profile changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.perfilState !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_profileState",data.perfilState).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' profileState changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.telefoneFixo !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_housePhone",data.telefoneFixo).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' home phone changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.telemovel !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_phone",data.telemovel).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' phone changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.morada !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_adress1",data.morada).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' adress1 changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.moradaComplementar !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_adress2",data.moradaComplementar).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' adress2 changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.localidade !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_local",data.localidade).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' local changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}
			if(data.cp !=null) {
				user= Entity.newBuilder(txn.get(userkey)).set("user_postcode",data.cp).build();
				txn.update(user);
				LOG.info("User '" + data.token.username + "' postcode changed sucessfully.");
				txn.commit();
				txn = datastore.newTransaction();
				user = txn.get(userkey);
			}





			

			return Response.ok("User '" + data.token.username + "' atributes modified").build();
		}

		}catch(Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}finally{
			if(txn.isActive())
			txn.rollback();
		}
		
	}
}
