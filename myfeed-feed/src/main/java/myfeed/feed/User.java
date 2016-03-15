package myfeed.feed;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author Spencer Gibb
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private String userId;
	private String username;
	private String name;
	private List<String> following;

	public User(String userId, String username, String name) {
		this.userId = userId;
		this.username = username;
		this.name = name;
	}
}
