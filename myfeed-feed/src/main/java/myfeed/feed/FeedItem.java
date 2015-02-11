package myfeed.feed;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * @author Spencer Gibb
 */
@Data
@Document
public class FeedItem {

	@Id
	private final String id;

	@NotNull
	private final String userid;

	@NotNull
	private final String username;

	@TextIndexed
	private final String text;

	@Indexed
	private final Date created;

	public FeedItem(String userid, String username, String text) {
		this(userid, username, text, new Date());
	}

	protected FeedItem(String userid, String username, String text, Date created) {
		this.id = UUID.randomUUID().toString();
		this.userid = userid;
		this.username = username;
		this.text = text;
		this.created = created;
	}

	//for serialization
	private FeedItem() {
		this.id = null;
		this.userid = null;
		this.username = null;
		this.text = null;
		this.created = null;
	}
}
