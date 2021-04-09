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


@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {
	
	

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public LogoutResource() {}
	
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(AuthToken token) {
		LOG.fine("Logout attempt by user: " + token.username);
		
		if(!token.isValid()) { //token nao e valido ou expirou
			return Response.status(Status.BAD_REQUEST).entity("Session expired").build();
		}
		Transaction txn = datastore.newTransaction();
		
		try {
		Key tokenkey = datastore.newKeyFactory().setKind("Token").newKey(token.tokenID);
		txn.delete(tokenkey);
		LOG.info("User logged out: " + token.username);
		txn.commit();
		return Response.ok("User '" + token.username + "' logged out").build();
		
		}finally {
			if(txn.isActive())
				txn.rollback();
		}
		
		
	}

}
