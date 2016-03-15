package myfeed.feed;

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
}
