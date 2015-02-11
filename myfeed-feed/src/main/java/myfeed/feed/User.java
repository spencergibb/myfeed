package myfeed.feed;

import lombok.Data;

/**
* @author Spencer Gibb
*/
@Data
public class User {
	private final String id;
	private final String username;
	private final String name;

	private User() {
		id = null;
		username = null;
		name = null;
	}
}
