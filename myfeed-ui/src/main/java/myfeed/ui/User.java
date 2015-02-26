package myfeed.ui;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
* @author Spencer Gibb
*/
@Data
@NoArgsConstructor
class User {
	private String userId;
	private String username;
	private String name;
	private List<String> following = new ArrayList<>();
}
