package myfeed.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Spencer Gibb
 */
@Data
@RedisHash("users")
public class User {

	@Id
	private final String id;

	@Indexed
	private final String username;

	@Indexed
	private final String name;

	private final List<String> following = new ArrayList<>();

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

	@JsonProperty("userId")
	public String getId() {
		return id;
	}
}
