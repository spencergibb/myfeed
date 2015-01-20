package myfeed.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Spencer Gibb
 */
@Data
@Document
public class User {

	@Id
	private final String id;

	@Indexed(unique = true)
	private final String username;

	@TextIndexed
	private final String name;

	public User(String username, String name) {
		this.username = username;
		this.name = name;
		this.id = null;
	}

	protected User() {
		this.username = null;
		this.name = null;
		this.id = null;
	}
}
