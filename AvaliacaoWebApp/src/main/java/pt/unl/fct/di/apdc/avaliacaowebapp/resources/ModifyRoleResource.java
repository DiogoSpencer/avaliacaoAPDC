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


import pt.unl.fct.di.apdc.avaliacaowebapp.util.ModifyRoleData;

@Path("/modifyRole")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyRoleResource {




	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public ModifyRoleResource() {}



	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modifyRole(ModifyRoleData data) {
		LOG.fine("Modify role attempt by user: " + data.token.username);

		
		
		
		if(!data.token.isValid()) { //token nao e valido ou expirou
			return Response.status(Status.BAD_REQUEST).entity("Session expired, login again.").build();
		}
		
		if(!data.validRoleModification()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong paramenter").build();
		}

		if(data.token.role.equals("USER") || data.token.role.equals("GBO")) {
			LOG.warning("User '" + data.token.username +"' does not have permissions for changing roles.");
			return Response.status(Status.BAD_REQUEST).entity("Only GA and SU users have permissions to change roles.").build();
		}else {
			Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.usernameToModifyRole);
			Entity user = datastore.get(userkey);

			if(user == null) {
				LOG.warning("User' " + data.usernameToModifyRole + "' does not exist.");
				return Response.status(Status.FORBIDDEN).build();
			}


			String userToModifyRole = user.getString("user_role");


			//SU: User -> GBO|GA
			//GA:User ->GBO
			if(userToModifyRole.equals("USER")) {
				if(data.token.role.equals("GA") && data.newRole.equals("GBO")) {
					 user= Entity.newBuilder(datastore.get(userkey)).set("user_role", data.newRole).build();
					 datastore.update(user);
					 LOG.info("User '" + data.usernameToModifyRole + "' role changed sucessfully.");
					 return Response.ok(g.toJson(data.token)).build();
				}else if(data.token.role.equals("SU")){
                    	  user= Entity.newBuilder(datastore.get(userkey)).set("user_role", data.newRole).build();
     					  datastore.update(user);
     					 LOG.info("User '" + data.usernameToModifyRole + "' role changed sucessfully.");
     					 return Response.ok(g.toJson(data.token)).entity("User role modified").build();
                      
				}

			}else {
				LOG.warning("User '" + data.token.username + "' cant modify '" + data.usernameToModifyRole + "' role.");
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		 return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		


		



	}

}
