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
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaowebapp.util.AccountStateData;

@Path("/modifyState")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class AccountStateResource {



	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public AccountStateResource() {}

	@POST
	@Path("/v3")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modifyAccountState(AccountStateData data) {

		LOG.fine("Modify state attempt by user: " + data.token.username);
		
		if(!data.token.isValid()) { //token nao e valido ou expirou
			return Response.status(Status.BAD_REQUEST).entity("Session expired, login again.").build();
		}


		if(!data.validModification()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong paramenter").build();
		}


		if(data.token.role.equals("USER")) {
			LOG.warning("User '" + data.token.username +"' does not have permissions for changing account states.");
			return Response.status(Status.BAD_REQUEST).entity("Users with the role USER dont have permissions to change account states.").build();

		}else {

			Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.userIdOfAccountToChange);
			Entity user = datastore.get(userkey);

			if(user == null) {
				LOG.warning("User' " + data.userIdOfAccountToChange + "' does not exist.");
				return Response.status(Status.FORBIDDEN).entity("User' " + data.userIdOfAccountToChange + "' does not exist.").build();
			}

			String userToModifyRole = user.getString("user_role");

			String userState = user.getString("user_state");
			String newState = null;

			if(userState.equals("ENABLED"))
				newState = "DISABLED";
			else if(userState.equals("DISABLED"))
				newState = "ENABLED";

			if(data.token.role.equals("GBO")) {
				if(userToModifyRole.equals("USER")) {
					user= Entity.newBuilder(datastore.get(userkey)).set("user_state", newState).build();
					datastore.update(user);
					LOG.info("User '" + data.userIdOfAccountToChange + "' state changed sucessfully.");
					return Response.ok(g.toJson(data.token)).entity("User '" + data.userIdOfAccountToChange + "' state changed sucessfully.").build();
				}else {
					LOG.warning("Users GBO can only modify the state of USER accounts.");
					return Response.status(Status.FORBIDDEN).entity("Users GBO can only modify the state of USER accounts.").build();
				}


			}else if(data.token.role.equals("GA")) {
				if(userToModifyRole.equals("USER") || userToModifyRole.equals("GBO")) {
					user= Entity.newBuilder(datastore.get(userkey)).set("user_state", newState).build();
					datastore.update(user);
					LOG.info("User '" + data.userIdOfAccountToChange + "' state changed sucessfully.");
					return Response.ok(g.toJson(data.token)).entity("User '" + data.userIdOfAccountToChange + "' state changed sucessfully.").build();
				}else {
					LOG.warning("Users GA can only modify the state of USER and GBO accounts.");
					return Response.status(Status.FORBIDDEN).entity("Users GA can only modify the state of USER and GBO accounts.").build();
				}

			}else if(data.token.role.equals("SU")) {
				if(!userToModifyRole.equals("SU")) {
					user= Entity.newBuilder(datastore.get(userkey)).set("user_state", newState).build();
					datastore.update(user);
					LOG.info("User '" + data.userIdOfAccountToChange + "' state changed sucessfully.");
					return Response.ok(g.toJson(data.token)).entity("User '" + data.userIdOfAccountToChange + "' state changed sucessfully.").build();
				}else {
					LOG.warning("Users SU can only modify the state of USER, GBO and GA  accounts.");
					return Response.status(Status.FORBIDDEN).entity("Users SU can only modify the state of USER, GBO and GA  accounts.").build();
				}
			}
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("diogo churro").build();






	}


}
