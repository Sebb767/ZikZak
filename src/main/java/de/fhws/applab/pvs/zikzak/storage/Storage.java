package de.fhws.applab.pvs.zikzak.storage;

import de.fhws.applab.pvs.zikzak.models.Message;
import de.fhws.applab.pvs.zikzak.models.User;

import java.util.*;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
public class Storage
{
	private static Storage INSTANCE;

	public static Storage getInstance()
	{
		if ( INSTANCE == null )
		{
			INSTANCE = new Storage( );
		}

		return INSTANCE;
	}

	private static long ID_COUNTER = 0L;

	private static long getNextID()
	{
		return ID_COUNTER++;
	}

	private Map<String, User> userStorage;

	private Map<Long, Message> messageStorage;

	private Map<String, Set<Long>> userToMessageMap;

	private Storage( )
	{
		userStorage = new HashMap<>( );
		messageStorage = new HashMap<>( );
		userToMessageMap = new HashMap<>( );
	}

	public String createUser( User user )
	{
		UUID uuid = UUID.randomUUID( );

		user.setId( uuid.toString( ) );

		userStorage.put( user.getId( ), user );

		return user.getId( );
	}

	public User getUser( String id )
	{
		return userStorage.get( id );
	}

	public void deleteUser( String id )
	{
		userStorage.remove( id );
	}

	public User updateUser( String id, User user )
	{
		user.setId( id );

		userStorage.put( id, user );

		return user;
	}

	public long createMessage( Message message )
	{
		long id = getNextID( );

		message.setId( id );

		messageStorage.put( id, message );

		return id;
	}

	public Message getMessage( long id )
	{
		return messageStorage.get( id );
	}

	public void deleteMessage( long id )
	{
		messageStorage.remove( id );
	}

	public Message updateMessage( long id, Message message )
	{
		message.setId( id );

		messageStorage.put( id, message );

		return message;
	}

	public void addMessageToUser( String userId, long messageId )
	{
		Set<Long> messages = userToMessageMap.get( userId );

		if ( messages == null )
		{
			messages = new HashSet<>( );
		}

		messages.add( messageId );

		userToMessageMap.put( userId, messages );
	}

	public void removeMessageFromUser( String userId, long messageId )
	{
		Set<Long> messages = userToMessageMap.get( userId );

		if ( messages != null )
		{
			messages.remove( messageId );
		}

	}

	public List<Message> getMessagesOfUser( String userId )
	{
		List<Message> messages = new ArrayList<>( );
		Set<Long> messageIds = userToMessageMap.get( userId );

		if ( messageIds != null )
		{
			for ( Long messageId : messageIds )
			{
				messages.add( messageStorage.get( messageId ) );
			}
		}

		return messages;
	}
}
