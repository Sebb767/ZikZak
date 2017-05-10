package de.fhws.applab.pvs.zikzak.api;

import de.fhws.applab.pvs.zikzak.models.Message;
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
@Path( "messages" )
public class MessageService
{
	@Context
	private UriInfo uriInfo;

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public Response listAllMessages( @QueryParam( "size" ) @DefaultValue( "10" ) int size,
		@QueryParam( "offset" ) @DefaultValue( "0" ) int offset )
	{
		List<Message> messages = Storage.getInstance( ).getMessages( size, offset );

		return Response.ok( messages ).build( );
	}

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	public Response saveMessage( Message message )
	{
		try
		{
			Storage.getInstance( ).createMessage( message );
		}
		catch ( Exception e )
		{
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		URI location = uriInfo.getAbsolutePathBuilder( )
							  .path( Long.toString( message.getId( ) ) )
							  .build( );

		return Response.created( location )
					   .build( );
	}

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path( "{id}" )
	public Response getMessage( @PathParam( "id" ) long id )
	{
		Message message = Storage.getInstance( ).getMessage( id );

		if ( message == null )
		{
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}

		return Response.ok( message ).build( );
	}

	@DELETE
	@Path( "{id}" )
	public Response deleteMessage( @PathParam( "id" ) long id )
	{
		try
		{
			Storage.getInstance( ).deleteMessage( id );
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
	public Response updateMessage( @PathParam( "id" ) long id, Message message )
	{
		try
		{
			Storage.getInstance( ).updateMessage( id, message );
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
