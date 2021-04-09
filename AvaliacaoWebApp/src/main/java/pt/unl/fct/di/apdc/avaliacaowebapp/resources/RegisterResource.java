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
import com.google.cloud.datastore.*;
import com.google.gson.Gson;


import pt.unl.fct.di.apdc.avaliacaowebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {


	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Gson g = new Gson();

	public RegisterResource() {
	} // Nothing to be done here


	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {
		LOG.fine("Register attempt by user: " + data.username);

        
		
		if(data.token != null) {
			if(data.token.isValid()){
				LOG.warning("User is already logged in: " + data.username);
				return Response.status(Status.BAD_REQUEST).entity("You are already logged in, please logout if you want to register a new account.").build();
			}
		}
		
		
		
		if(!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong paramenter").build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userkey);

			if(user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists").build();
			}else {

				user = Entity.newBuilder(userkey)
						.set("user_name", data.username)
						.set("user_email", data.email)
						.set("user_pwd", DigestUtils.sha512Hex(data.password))
						.set("user_profile", "")
						.set("user_profileState", "PUBLIC")
						.set("user_state", "ENABLED")
						.set("user_housePhone", "")
						.set("user_phone", "")
						.set("user_adress1", "")
						.set("user_adress2", "")
						.set("user_local", "")
						.set("user_postcode", "")
						.set("user_role", "USER")
						.set("user_creation_time", Timestamp.now())
						.build();
				txn.add(user);
				LOG.info("User registered " + data.username);
				txn.commit();
				return Response.ok("{}").build();
			}

		}finally {
			if(txn.isActive())
				txn.rollback();


		}
	}








}