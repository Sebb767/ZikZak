package de.fhws.applab.pvs.zikzak.models;

import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import de.fhws.applab.pvs.zikzak.converter.LinkConverter;
import org.glassfish.jersey.linking.InjectLink;

import javax.ws.rs.core.Link;

/**
 * (c) Tobias Fertig, FHWS 2017
 */
public class User
{
	private String id;

	public User( )
	{

	}

	@JsonIgnore
	public String getId( )
	{
		return id;
	}

	@JsonIgnore
	public void setId( String id )
	{
		this.id = id;
	}

	@InjectLink(style = InjectLink.Style.ABSOLUTE, value = "users/${instance.id}/messages", type = "application/json",
		rel = "messageUrl")
	private Link messageUrl;

	@JsonConverter( LinkConverter.class )
	public Link getMessageUrl( )
	{
		return messageUrl;
	}

	@JsonIgnore
	public void setMessageUrl( Link messageUrl )
	{
		this.messageUrl = messageUrl;
	}

	@InjectLink(style = InjectLink.Style.ABSOLUTE, value = "users/${instance.id}", type = "application/json", rel=
		"selfUri")
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
