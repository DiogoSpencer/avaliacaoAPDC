package pt.unl.fct.di.apdc.avaliacaowebapp.resources;

import java.util.ArrayList;
import java.util.List;
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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.avaliacaowebapp.util.SearchForRoleData;


@Path("/searchForRole")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SearchUsersForRoleResource {
	
	
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public SearchUsersForRoleResource() {
		// TODO Auto-generated constructor stub
	}
	
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response SearchForRoleResourcer(SearchForRoleData data) {
           LOG.fine("Search for role attempt by user: " + data.token.username);
		
		if(!data.token.isValid()) { //token nao e valido ou expirou
			return Response.status(Status.BAD_REQUEST).entity("Session expired, login again.").build();
		}
		
		if(!data.validSearchForRole()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong paramenter").build();
		}

		if(data.token.role.equals("USER")) {
			LOG.warning("User '" + data.token.username +"' does not have permissions for this.");
			return Response.status(Status.BAD_REQUEST).entity("Only GBO, GA and SU users have permissions to use this search.").build();
		
	}
		
		
		Key userkey = datastore.newKeyFactory().setKind("User").newKey(data.token.username);
	
		Entity user = datastore.get(userkey);
		
		if(user == null) {    //username does not exist
			
			LOG.warning("Failed login attempt by user: " + data.token.username);
			return Response.status(Status.FORBIDDEN).entity("User '" + data.token.username + "' does not exist").build();
		}
		
		String state = user.getString("user_state");
		if(state.equals("DISABLED")) {  //account is disabled
			
			LOG.warning("This account is disabled: " + data.token.username);
			return Response.status(Status.FORBIDDEN).entity("User '" + data.token.username + "' is disabled").build();
		}
		
		LOG.info("User '" + data.token.username + "'  searching for users with role : " + data.role);
		
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("User")
				.setFilter(PropertyFilter.eq("user_role", data.role))
				.build();
		
		
		QueryResults<Entity> logs = datastore.run(query);
		
		List<String> usersWithThisRole = new ArrayList<String>();
		
		logs.forEachRemaining(userWithRole ->{
			usersWithThisRole.add(userWithRole.getString("user_name"));
		});
		
		
		
		 return Response.ok(g.toJson(data.token) + g.toJson(usersWithThisRole)).build();
		
		
		
		
	
		
	}

}
