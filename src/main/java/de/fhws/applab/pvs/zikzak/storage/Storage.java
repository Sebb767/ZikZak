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

	public List<User> getUsers( int size, int offset )
	{
		List<User> users = new ArrayList<>( );

		int count = 0;

		for ( User user : userStorage.values( ) )
		{
			if ( count >= offset && users.size( ) <= size )
			{
				users.add( user );
			}

			count++;
		}

		return users;
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

	public List<Message> getMessages( int size, int offset )
	{
		List<Message> messages = new ArrayList<>( );

		int count = 0;

		for ( Message message : messageStorage.values( ) )
		{
			if ( count >= offset && messages.size( ) <= size )
			{
				messages.add( message );
			}

			count++;

			if(count == 100)
			{
				break;
			}
		}

		return messages;
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

	public boolean ownsUserMessage( String userId, long id )
	{
		Set<Long> messageIds = userToMessageMap.get( userId );

		return messageIds.contains( id );
	}

	public List<Message> getMessagesOfUser( String userId, int size, int offset )
	{
		List<Message> messages = new ArrayList<>( );
		Set<Long> messageIds = userToMessageMap.get( userId );

		if ( messageIds != null )
		{
			int count = 0;

			for ( Long messageId : messageIds )
			{
				if ( count >= offset && messages.size( ) <= size )
				{
					messages.add( messageStorage.get( messageId ) );
				}
				count++;
			}
		}

		return messages;
	}
}
