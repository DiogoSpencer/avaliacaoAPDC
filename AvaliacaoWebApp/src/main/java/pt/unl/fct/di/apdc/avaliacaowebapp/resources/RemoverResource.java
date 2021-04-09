package pt.unl.fct.di.apdc.avaliacaowebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaowebapp.util.AuthToken;
import pt.unl.fct.di.apdc.avaliacaowebapp.util.LoginData;
import pt.unl.fct.di.apdc.avaliacaowebapp.util.RemoveData;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoverResource {



	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public RemoverResource() {
		// TODO Auto-generated constructor stub
	}




	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRemove(RemoveData data) {
		LOG.fine("Remove attempt by user: " + data.token.username);

		Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.token.username);
		
		
		Transaction txn = datastore.newTransaction();
		try {
			
		
		Entity user = txn.get(userkey);
		
		
		if(!data.token.isValid()) { //token nao e valido ou expirou
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity("Session expired, login again.").build();
		}


		if(user == null) {    //not logged in
			txn.rollback();
			LOG.warning("Not logged in");
			return Response.status(Status.FORBIDDEN).build();
		}
		String role = user.getString("user_role");

		if(role.equals("USER")) {
			if(data.userToDelete != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("USER role can only delete himself.").build();
			}
			//user= Entity.newBuilder(datastore.get(userkey)).set("user_profileState","DELETED").build();
			txn.delete(userkey);
			LOG.info("User '" + data.token.username + "' deleted is account sucessfully.");
			txn.commit();
			return Response.ok("Account deleted").build();
		}else if(role.equals("GBO") || role.equals("GA")) {
			if(data.userToDelete == null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Missing paramenter").build();
			}
			
			userkey = datastore.newKeyFactory().setKind("User").newKey(data.userToDelete);
			user = txn.get(userkey);
			if(user == null) {    //User to delete does not exist
				txn.rollback();
				LOG.warning("User to delete does not exist.");
				return Response.status(Status.BAD_REQUEST).build();
			}

		//	user= Entity.newBuilder(datastore.get(userkey)).set("user_profileState","DELETED").build();
			
			txn.delete(userkey);
			LOG.info("User deleted: " + data.userToDelete);
			txn.commit();
			return Response.ok("Account of user '" + data.userToDelete + "' deleted").build();

		}
		txn.rollback();
		return Response.status(Status.BAD_REQUEST).entity("SU cant delete users").build();





		}finally {
			if(txn.isActive())
				txn.rollback();

	}
	}
}



