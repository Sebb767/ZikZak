package de.fhws.applab.pvs.zikzak.api;

/**
 * (c) Tobias Fertig, FHWS 2017
 */

import de.fhws.applab.pvs.zikzak.models.Message;
import de.fhws.applab.pvs.zikzak.storage.Storage;
import de.fhws.applab.pvs.zikzak.utils.Hyperlinks;

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

		return Response.ok( messages )
					   .header( "Link", createPostMessageConnectionHeader( ) )
					   .header( "Link", createSelfUriForPage( ) )
					   .header( "Link", createPrevUriForPage( size, offset ) )
					   .header( "Link", createNextUriForPage( userid, size, offset ) )
					   .header( "X-totalnumberofresults", createTotalNumberHeader( userid ) )
					   .header( "X-numberofresults", messages.size( ) )
					   .build( );
	}

	private int createTotalNumberHeader( String userid )
	{
		return Storage.getInstance( ).getTotalNumberOfMessagesOfUser( userid );
	}

	private String createSelfUriForPage()
	{
		return Hyperlinks.linkHeader( uriInfo.getRequestUri( ).toString( ), "self", MediaType.APPLICATION_JSON );
	}

	private String createNextUriForPage(String userid, int size, int offset)
	{
		int totalNumber = Storage.getInstance( ).getTotalNumberOfMessagesOfUser( userid );

		int nextOffset = Math.min( offset + size, totalNumber );

		URI location = uriInfo.getBaseUriBuilder( )
							  .path( this.getClass( ) )
							  .build( );

		String nextUri = location.toString() + "?size=" + size + "&offset=" + nextOffset;

		return Hyperlinks.linkHeader( nextUri, "next", MediaType.APPLICATION_JSON );
	}

	private String createPrevUriForPage(int size, int offset)
	{
		int prevOffset = Math.max(offset - size, 0);

		URI location = uriInfo.getBaseUriBuilder( )
							  .path( this.getClass( ) )
							  .build( );

		String prevUri = location.toString() + "?size=" + size + "&offset=" + prevOffset;

		return Hyperlinks.linkHeader( prevUri, "prev", MediaType.APPLICATION_JSON );
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

		return Response.ok( message )
					   .header( "Link", createGetAllMessageConnectionsHeader( ) )
					   .header( "Link", createDeleteMessageConnectionHeader( ) )
					   .build( );
	}

	@POST
	@Path( "{id}" )
	public Response addConnection( @PathParam( "userid" ) String userid,
		@PathParam( "id" ) long id )
	{
		Storage.getInstance( ).addMessageToUser( userid, id );

		URI location = uriInfo.getAbsolutePathBuilder( ).build( );

		return Response.created( location )
					   .header( "Link", createGetAllMessageConnectionsHeader( ) )
					   .build( );
	}

	@DELETE
	@Path( "{id}" )
	public Response removeConnection( @PathParam( "userid" ) String userid,
		@PathParam( "id" ) long id )
	{
		Storage.getInstance( ).removeMessageFromUser( userid, id );

		return Response.noContent( )
					   .header( "Link", createGetAllMessageConnectionsHeader( ) )
					   .build( );
	}

	@GET
	@Path( "ping" )
	public String ping( )
	{
		return "OK";
	}

	private String createGetAllMessageConnectionsHeader( )
	{
		URI location = uriInfo.getBaseUriBuilder( )
							  .path( this.getClass( ) )
							  .build( );

		String uri = location.toString() + "?size={SIZE}&offset={OFFSET}";

		return Hyperlinks.linkHeader( uri, "getAllMessagesOfUser", MediaType.APPLICATION_JSON );
	}

	private String createPostMessageConnectionHeader( )
	{
		URI location = uriInfo.getBaseUriBuilder( )
							  .path( this.getClass( ) )
							  .build( );

		return Hyperlinks.linkHeader( location.toString( ), "createMessageConnection", MediaType.APPLICATION_JSON );
	}

	private String createDeleteMessageConnectionHeader()
	{
		URI location = uriInfo.getRequestUri( );

		return Hyperlinks.linkHeader( location.toString( ), "deleteMessageConnection", MediaType.APPLICATION_JSON );
	}
}
