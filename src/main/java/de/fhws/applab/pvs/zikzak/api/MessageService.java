package de.fhws.applab.pvs.zikzak.api;

import de.fhws.applab.pvs.zikzak.models.Message;
import de.fhws.applab.pvs.zikzak.models.Vote;
import de.fhws.applab.pvs.zikzak.storage.Storage;
import de.fhws.applab.pvs.zikzak.utils.Hyperlinks;

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

		return Response.ok( messages )
					   .header( "Link", createPostMessageHeader( ) )
					   .header( "Link", createSelfUriForPage( ) )
					   .header( "Link", createPrevUriForPage( size, offset ) )
					   .header( "Link", createNextUriForPage( size, offset ) )
					   .header( "X-totalnumberofresults", createTotalNumberHeader( ) )
					   .header( "X-numberofresults", messages.size( ) )
					   .build( );
	}

	private int createTotalNumberHeader( )
	{
		return Storage.getInstance( ).getTotalNumberOfMessages( );
	}

	private String createSelfUriForPage()
	{
		return Hyperlinks.linkHeader( uriInfo.getRequestUri( ).toString( ), "self", MediaType.APPLICATION_JSON );
	}

	private String createNextUriForPage(int size, int offset)
	{
		int totalNumber = Storage.getInstance( ).getTotalNumberOfMessages( );

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
					   .header( "Link", createGetAllMessagesHeader( ) )
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

		return Response.ok( message )
					   .header( "Link", createGetAllMessagesHeader( ) )
					   .header( "Link", createPutMessageHeader( ) )
					   .header( "Link", createDeleteMessageHeader( ) )
					   .build( );
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

		return Response.noContent( )
					   .header("Link", createGetAllMessagesHeader())
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

		return Response.noContent( )
					   .header( "Link", createGetAllMessagesHeader( ) )
					   .header( "Link", createGetSingleMessageHeader( ) )
					   .build( );
	}

	@PUT
	@Path( "{id}/votes" )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response doVote( @PathParam( "id" ) long id, Vote vote)
	{
		Message message = Storage.getInstance( ).getMessage( id );

		if(message == null)
		{
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}

		message.addVote( vote );

		return Response.noContent( ).build( );
	}

	@PUT
	@Path( "{id}/downvotes" )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response doDownvote( @PathParam( "id" ) long id, String userid )
	{
		return doVote(id, new Vote(userid, id, (short)-1));
	}

	@PUT
	@Path( "{id}/upvotes" )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response doUpvote( @PathParam( "id" ) long id, String userid )
	{
		return doVote(id, new Vote(userid, id, (short)1));
	}

	@GET
	@Path( "ping" )
	public String ping( )
	{
		return "OK";
	}

	private String createGetAllMessagesHeader( )
	{
		URI location = uriInfo.getBaseUriBuilder( )
							  .path( this.getClass( ) )
							  .build( );

		String uri = location.toString() + "?size={SIZE}&offset={OFFSET}";

		return Hyperlinks.linkHeader( uri, "getAllMessages", MediaType.APPLICATION_JSON );
	}

	private String createPostMessageHeader( )
	{
		URI location = uriInfo.getBaseUriBuilder( )
							  .path( this.getClass( ) )
							  .build( );

		return Hyperlinks.linkHeader( location.toString( ), "createMessage", MediaType.APPLICATION_JSON );
	}

	private String createGetSingleMessageHeader()
	{
		URI location = uriInfo.getRequestUri( );

		return Hyperlinks.linkHeader( location.toString( ), "getSingleMessage", MediaType.APPLICATION_JSON );
	}

	private String createPutMessageHeader()
	{
		URI location = uriInfo.getRequestUri( );

		return Hyperlinks.linkHeader( location.toString( ), "updateMessage", MediaType.APPLICATION_JSON );
	}

	private String createDeleteMessageHeader()
	{
		URI location = uriInfo.getRequestUri( );

		return Hyperlinks.linkHeader( location.toString( ), "deleteMessage", MediaType.APPLICATION_JSON );
	}
}
