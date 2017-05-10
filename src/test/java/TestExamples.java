import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import de.fhws.applab.pvs.zikzak.models.Message;
import okhttp3.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
public class TestExamples
{
	private final static MediaType JSON = MediaType.parse( "application/json; charset=utf-8" );

	private final static String BASE_URL = "http://localhost:8080/zikzak/api";

	private OkHttpClient client;

	private Genson genson;

	@Before
	public void setUp()
	{
		client = new OkHttpClient( );

		genson = new GensonBuilder( ).setSkipNull( true )
									 .useIndentation( true )
									 .useDateAsTimestamp( false )
									 .useDateFormat( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ) )
									 .create( );
	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void testPost()
	{
		Request request = new Request.Builder( ).url( BASE_URL ).get( ).build( );
		Response response = executeRequest( request );

		String messageUrl = getMessageUrl( response );

		assertTrue( "Hyperlink was not set!", !messageUrl.equals( "" ) );

		Message message = new Message( );
		message.setText( "hallo das ist eine message" );

		RequestBody body = RequestBody.create( JSON, genson.serialize( message ) );

		request = new Request.Builder( ).url( messageUrl ).post( body ).build( );

		response = executeRequest( request );

		assertTrue( "Object was not created!", response.code( ) == 201 );
	}

	private String getMessageUrl( Response response )
	{
		for ( String link : response.headers( "Link" ) )
		{
			String[] parts = link.split( ";" );

			if ( parts[ 1 ].equals( "rel=\"getAllMessages\"" ) )
			{
				return parts[ 0 ].substring( 1, parts[ 0 ].length( ) - 1 );
			}
		}

		return "";
	}

	@Test
	public void testGet()
	{
		Request request = new Request.Builder( ).url( BASE_URL ).get( ).build( );
		Response response = executeRequest( request );

		String messageUrl = getMessageUrl( response );

		assertTrue( "Hyperlink was not set!", !messageUrl.equals( "" ) );

		request = new Request.Builder( ).url( messageUrl ).get( ).build( );

		response = executeRequest( request );

		assertTrue( "Get request failed!", response.code( ) == 200 );
	}

	private Response executeRequest( Request request )
	{
		Response response;

		try
		{
			response = client.newCall( request ).execute( );
		}
		catch ( IOException e )
		{
			response = null;
		}

		return response;
	}
}
