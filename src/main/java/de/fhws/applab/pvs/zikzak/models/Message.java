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

	private Set<Vote> votes;

	@JsonDateFormat("yyyy-MM-dd'T'HH:mm:ss")
	private Date creationDate;

	public Message( )
	{
		this.votes = new HashSet<>();
		this.creationDate = new Date( );
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

	public int getScore() {
		return votes.stream().mapToInt(Vote::getScore).sum();
	}

	@InjectLink(style = InjectLink.Style.ABSOLUTE, value = "messages/${instance.id}/votes", type =
		"application/json",
		rel = "putVote")
	private Link voteLink;

	public Set<Vote> getVotes() {
		return votes;
	}

	public void addVote(Vote vote) {
		this.votes.add(vote);
	}

	@JsonConverter( LinkConverter.class )
	public Link getVoteLink() {
		return voteLink;
	}

	@JsonIgnore
	public void setVoteLink(Link voteLink) {
		this.voteLink = voteLink;
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

	@InjectLink(style = InjectLink.Style.ABSOLUTE, value = "messages/${instance.id}", type = "application/json", rel=
		"self")
	private Link selfUri;

	@JsonConverter( LinkConverter.class )
	public Link getSelfUri( )
	{
		return selfUri;
	}

	@JsonIgnore
	public void setSelfUri( Link selfUri )
	{
		this.selfUri = selfUri;
	}
}
