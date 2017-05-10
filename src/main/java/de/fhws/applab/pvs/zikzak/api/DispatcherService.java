package de.fhws.applab.pvs.zikzak.api;

import de.fhws.applab.pvs.zikzak.utils.Hyperlinks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
@Path( "/" )
public class DispatcherService
{
	@Context UriInfo uriInfo;


	@GET
	@Produces( MediaType.APPLICATION_JSON )

	public Response getBase( )
	{
		String userUri = uriInfo.getAbsolutePathBuilder( ).path( "users" ).build( new Object[ 0 ] ).toString( );
		String messageUri = uriInfo.getAbsolutePathBuilder( ).path( "messages" ).build( new Object[ 0 ] ).toString( );

		return Response.ok( )
					   .header( "Link", Hyperlinks.linkHeader( userUri, "getAllUsers", MediaType.APPLICATION_JSON ) )
					   .header( "Link", Hyperlinks.linkHeader( messageUri, "getAllMessages", MediaType.APPLICATION_JSON ) )
					   .build( );
	}
}

