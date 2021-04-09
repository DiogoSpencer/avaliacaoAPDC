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


import pt.unl.fct.di.apdc.avaliacaowebapp.util.SearchForUserInfoData;
import pt.unl.fct.di.apdc.avaliacaowebapp.util.UserInfo;


@Path("/searchForUserInfo")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SearchForUserInfo {
	
	
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// A Logger Object
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	public SearchForUserInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response SearchForRoleResourcer(SearchForUserInfoData data) {
           LOG.fine("Search for user info attempt by user: " + data.token.username);
		
		if(!data.token.isValid()) { //token nao e valido ou expirou
			return Response.status(Status.BAD_REQUEST).entity("Session expired, login again.").build();
		}
		
		if(!data.validSearchForUserInfo()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong paramenter").build();
		}

		if(data.token.role.equals("USER") || data.token.role.equals("GBO") || data.token.role.equals("GA")) {
			LOG.warning("User '" + data.token.username +"' does not have permissions for this.");
			return Response.status(Status.BAD_REQUEST).entity("Only SU users have permissions to use this search.").build();
		
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
		
		
		Key userToSearchKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity userToSearch = datastore.get(userToSearchKey);
		
        if(userToSearch == null) {    //user does not exist
			
			LOG.warning("User '" + data.username + "' does not exist.");
			return Response.status(Status.FORBIDDEN).entity("User '" + data.username + "' does not exist.").build();
		}
		
		LOG.info("User '" + data.token.username + "'  searching for user '" + data.username + "' info.");
		
		
		
		UserInfo info = new UserInfo(userToSearch.getString("user_name"),
				userToSearch.getString("user_email"),
				userToSearch.getString("user_pwd"),
				userToSearch.getString("user_role"),
				userToSearch.getString("user_profile"),
				userToSearch.getString("user_profileState"),
				userToSearch.getString("user_housePhone"),
				userToSearch.getString("user_phone"),
				userToSearch.getString("user_adress1"),
				userToSearch.getString("user_adress2"),
				userToSearch.getString("user_local"),
				userToSearch.getString("user_postcode"),
				userToSearch.getString("user_state"),
				userToSearch.getTimestamp("user_creation_time")
					);


		
		
		
		 return Response.ok(g.toJson(data.token) + g.toJson(info)).build();
		
		
		
		
	
		
	}

}
