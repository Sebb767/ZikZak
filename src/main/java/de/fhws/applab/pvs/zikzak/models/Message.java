package de.fhws.applab.pvs.zikzak.models;

import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonDateFormat;
import com.owlike.genson.annotation.JsonIgnore;
import de.fhws.applab.pvs.zikzak.converter.LinkConverter;
import org.glassfish.jersey.linking.InjectLink;

import javax.ws.rs.core.Link;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
public class Message
{
	private long id;

	private String text;

	private Set<String> upVotes;

	private Set<String> downVotes;

	@JsonDateFormat("yyyy-MM-dd'T'HH:mm:ss")
	private Date creationDate;

	public Message( )
	{
		this.upVotes = new HashSet<>( );

		this.downVotes = new HashSet<>( );
	}

	public long getId( )
	{
		return id;
	}

	@JsonIgnore
	public void setId( long id )
	{
		this.id = id;
	}

	public String getText( )
	{
		return text;
	}

	public void setText( String text )
	{
		this.text = text;
	}

	public int getUpVotes()
	{
		return upVotes.size( );
	}

	public void addUpVote( String userId )
	{
		upVotes.add( userId );
	}

	public int getDownVotes()
	{
		return downVotes.size( );
	}

	public void addDownVote( String userId )
	{
		downVotes.add( userId );
	}

	@InjectLink(style = InjectLink.Style.ABSOLUTE, value = "messages/${instance.id}/upvotes", type =
		"application/json",
		rel = "putUpvote")
	private Link upVoteLink;

	@JsonConverter( LinkConverter.class )
	public Link getUpVoteLink( )
	{
		return upVoteLink;
	}

	@JsonIgnore
	public void setUpVoteLink( Link upVoteLink )
	{
		this.upVoteLink = upVoteLink;
	}

	@InjectLink(style = InjectLink.Style.ABSOLUTE, value = "messages/${instance.id}/downvotes", type =
		"application/json",
		rel = "putDownvote")
	private Link downVoteLink;

	@JsonConverter( LinkConverter.class )
	public Link getDownVoteLink( )
	{
		return downVoteLink;
	}

	@JsonIgnore
	public void setDownVoteLink( Link downVoteLink )
	{
		this.downVoteLink = downVoteLink;
	}


	public Date getCreationDate( )
	{
		return creationDate;
	}

	@JsonIgnore
	public void setCreationDate( Date creationDate )
	{
		this.creationDate = creationDate;
	}
}
