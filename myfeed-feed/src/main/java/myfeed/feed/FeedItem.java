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

	@TextIndexed
	private final String text;

	@Indexed
	private final Date created;

	public FeedItem(String userid, String text) {
		this.id = UUID.randomUUID().toString();
		this.userid = userid;
		this.text = text;
		created = new Date();
	}
}
