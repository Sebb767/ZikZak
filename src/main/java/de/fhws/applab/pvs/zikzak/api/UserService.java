package de.fhws.applab.pvs.zikzak.api;

import de.fhws.applab.pvs.zikzak.models.User;
import de.fhws.applab.pvs.zikzak.storage.Storage;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
@Path( "users" )
public class UserService
{
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response listAllUsers( @QueryParam( "size" ) @DefaultValue( "10" ) int size,
		@QueryParam( "offset" ) @DefaultValue( "0" ) int offset )
	{
		List<User> users = Storage.getInstance( ).getUsers( size, offset );

		return Response.ok( users ).build( );
	}

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	public Response saveUser( User user )
	{
		try
		{
			Storage.getInstance( ).createUser( user );
		}
		catch ( Exception e )
		{
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		URI location = uriInfo.getAbsolutePathBuilder( )
							  .path( user.getId( ) )
							  .build( );

		return Response.created( location )
					   .build( );
	}

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path( "{id}" )
	public Response getUser( @PathParam( "id" ) String id )
	{
		User user = Storage.getInstance( ).getUser( id );

		if ( user == null )
		{
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}

		return Response.ok( user ).build( );
	}

	@DELETE
	@Path( "{id}" )
	public Response deleteUser( @PathParam( "id" ) String id )
	{
		try
		{
			Storage.getInstance( ).deleteUser( id );
		}
		catch ( Exception e )
		{
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		URI location = uriInfo.getBaseUriBuilder( ).path( this.getClass( ) ).build( );

		return Response.noContent( )
					   .contentLocation( location )
					   .build( );
	}

	@PUT
	@Consumes( MediaType.APPLICATION_JSON )
	@Path( "{id}" )
	public Response updateUser( @PathParam( "id" ) String id, User user )
	{
		try
		{
			Storage.getInstance( ).updateUser( id, user );
		}
		catch ( Exception e )
		{
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		URI location = uriInfo.getRequestUri( );

		return Response.noContent( )
					   .contentLocation( location )
					   .build( );
	}

	@GET
	@Path( "ping" )
	public String ping( )
	{
		return "OK";
	}
}
