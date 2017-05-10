package de.fhws.applab.pvs.zikzak.utils;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
public class Hyperlinks
{
	public static String linkHeader( String uri, String rel, String mediaType )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( '<' ).append( uri ).append( ">;" );
		sb.append( "rel" ).append( "=\"" ).append( rel ).append( "\"" );
		if ( mediaType != null && !mediaType.isEmpty( ) )
		{
			sb.append( ";" );
			sb.append( "type" ).append( "=\"" ).append( mediaType ).append( "\"" );
		}

		return sb.toString( );
	}
}
