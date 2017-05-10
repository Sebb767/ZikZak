package de.fhws.applab.pvs.zikzak.api;

/**
 * (c) Tobias Fertig, FHWS 2017
 */

import de.fhws.applab.pvs.zikzak.models.Message;
import de.fhws.applab.pvs.zikzak.storage.Storage;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("users/{userid}/messages")
public class MessageSecondaryService
{
	@Context private UriInfo uriInfo;

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response listAllMessagesOfUser( @PathParam( "userid" ) String userid,
		@QueryParam( "size" ) @DefaultValue( "10" ) int size,
		@QueryParam( "offset" ) @DefaultValue( "0" ) int offset )
	{
		List<Message> messages = Storage.getInstance( ).getMessagesOfUser( userid, size, offset );

		return Response.ok( messages ).build( );
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessageOfUser( @PathParam( "userid" ) String userid,
		@PathParam( "id" ) long id )
	{
		if(!Storage.getInstance().ownsUserMessage( userid, id ))
		{
			throw new WebApplicationException( Response.Status.FORBIDDEN );
		}

		Message message = Storage.getInstance( ).getMessage( id );

		if ( message == null )
		{
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}

		return Response.ok( message ).build( );
	}

	@POST
	@Path( "{id}" )
	public Response addConnection( @PathParam( "userid" ) String userid,
		@PathParam( "id" ) long id )
	{
		Storage.getInstance( ).addMessageToUser( userid, id );

		URI location = uriInfo.getAbsolutePathBuilder( ).build( );

		return Response.created( location ).build( );
	}

	@DELETE
	@Path( "{id}" )
	public Response removeConnection( @PathParam( "userid" ) String userid,
		@PathParam( "id" ) long id )
	{
		Storage.getInstance( ).removeMessageFromUser( userid, id );

		return Response.noContent( ).build( );
	}

	@GET
	@Path( "ping" )
	public String ping( )
	{
		return "OK";
	}
}
